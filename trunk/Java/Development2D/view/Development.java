package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import development.AffineTransformation;
import development.Coord2D;
import development.CoordTrans2D;
import development.EmbeddedFace;
import development.Frustum2D;
import development.Vector;

import triangulation.Edge;
import triangulation.Face;
import triangulation.Vertex;

public class Development extends Observable {
	private DevelopmentNode root;
	private Vector sourcePoint;
	private Face sourceFace;
	private int maxDepth;
	private int desiredDepth; // the depth to which observers will want to show
								// it

	public Development(Face sourceF, Vector sourcePt, int max, int desired) {
		maxDepth = max;
		desiredDepth = desired;
		sourcePoint = sourcePt;
		sourceFace = sourceF;

		buildTree();
	}

	public DevelopmentNode getRoot() {
		return root;
	}

	public Vector getSourcePoint() {
		return sourcePoint;
	}

	public void setDesiredDepth(int d) {
		desiredDepth = d;
		setChanged();
		notifyObservers();
	}

	public int getDesiredDepth() {
		return desiredDepth;
	}
	
	public void setSourcePoint(Vector point) {
		sourcePoint = point;
		buildTree();
		setChanged();
		notifyObservers();
	}

	private void buildTree() {
		// get transformation taking sourcePoint to origin (translation by
		// -1*sourcePoint)
		AffineTransformation t = new AffineTransformation(Vector.scale(
				sourcePoint, -1));

		Vector.scale(sourcePoint, -1);

		EmbeddedFace transformedFace = t.affineTransFace(sourceFace);
		root = new DevelopmentNode(null, sourceFace, transformedFace);
		List<Vertex> vertices = sourceFace.getLocalVertices();
		for (int i = 0; i < vertices.size(); i++) {
			Vertex v0 = vertices.get(i);
			Vertex v1 = vertices.get((i + 1) % vertices.size());
			List<Edge> list0 = v0.getLocalEdges();
			List<Edge> list1 = v1.getLocalEdges();
			Edge edge = null;
			for (int j = 0; j < list0.size(); j++) {
				if (list1.contains(list0.get(i))) {
					edge = list0.get(i);
					break;
				}
			}

			// build frustum through edge end-points
			Vector vect0 = t.affineTransPoint(Coord2D.coordAt(v0, sourceFace));
			Vector vect1 = t.affineTransPoint(Coord2D.coordAt(v1, sourceFace));
			Frustum2D frustum = new Frustum2D(vect1, vect0);

			// each edge is adjacent to 2 faces; take the one that is not the
			// current face
			List<Face> faces = edge.getLocalFaces();
			Face newFace;
			if (faces.get(0) == sourceFace)
				newFace = faces.get(1);
			else
				newFace = faces.get(0);

			buildTree(root, newFace, edge, frustum, t, 1);
		}
	}

	private void buildTree(DevelopmentNode parent, Face face, Edge sourceEdge,
			Frustum2D frustum, AffineTransformation t, int depth) {

		AffineTransformation newTrans = new AffineTransformation(2);

		AffineTransformation coordTrans = CoordTrans2D.affineTransAt(face,
				sourceEdge);
		newTrans.leftMultiply(coordTrans);
		newTrans.leftMultiply(t);

		if (depth > maxDepth)
			return;

		EmbeddedFace clippedFace = frustum.clipFace(newTrans
				.affineTransFace(face));

		// quit if face is completely obscured
		if (clippedFace == null) {
			return;
		}

		DevelopmentNode node = new DevelopmentNode(parent, face, clippedFace);
		parent.addChild(node);

		if (depth == maxDepth)
			return;

		List<Vertex> vertices = face.getLocalVertices();
		for (int i = 0; i < vertices.size(); i++) {
			Vertex v0 = vertices.get(i);
			Vertex v1 = vertices.get((i + 1) % vertices.size());
			List<Edge> list0 = v0.getLocalEdges();
			List<Edge> list1 = v1.getLocalEdges();

			Edge edge = null;
			for (int j = 0; j < list0.size(); j++) {
				if (list1.contains(list0.get(j))) {
					edge = list0.get(j);
					break;
				}
			}
			if (edge.equals(sourceEdge)) {
				continue;
			}

			// build frustum through edge end-points
			Vector vect0 = newTrans.affineTransPoint(Coord2D.coordAt(v0, face));
			Vector vect1 = newTrans.affineTransPoint(Coord2D.coordAt(v1, face));
			Frustum2D newFrustum = Frustum2D.intersect(new Frustum2D(vect1,
					vect0), frustum);

			// each edge is adjacent to 2 faces; take the one that is not the
			// current face
			List<Face> faces = edge.getLocalFaces();
			Face newFace;
			if (faces.get(0) == face)
				newFace = faces.get(1);
			else
				newFace = faces.get(0);

			if (newFrustum != null)
				buildTree(node, newFace, edge, newFrustum, newTrans, depth + 1);
		}
	}

	public class DevelopmentNode {
		private EmbeddedFace embeddedFace;
		private Face face;
		private ArrayList<DevelopmentNode> children;
		private DevelopmentNode parent;
		private int depth;

		public DevelopmentNode(DevelopmentNode prev, Face f, EmbeddedFace ef,
				DevelopmentNode... nodes) {
			parent = prev;
			if (parent == null)
				depth = 0;
			else
				depth = parent.getDepth() + 1;
			embeddedFace = new EmbeddedFace(ef);
			face = f;
			children = new ArrayList<DevelopmentNode>();
			for (int i = 0; i < nodes.length; i++) {
				children.add(nodes[i]);
			}
		}

		public void addChild(DevelopmentNode node) {
			children.add(node);
		}

		public void removeChild(DevelopmentNode node) {
			children.remove(node);
		}

		public EmbeddedFace getEmbeddedFace() {
			return embeddedFace;
		}

		public Face getFace() {
			return face;
		}

		public int getDepth() {
			return depth;
		}

		public ArrayList<DevelopmentNode> getChildren() {
			return children;
		}

		public boolean isRoot() {
			return parent == null;
		}
	}
}
