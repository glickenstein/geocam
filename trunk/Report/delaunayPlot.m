% This program takes the output of generateTriangulation, saved as a text
% file,  and plots it in MATLAB. The Excel file stores the coordinates of
% each triangle by four vertices so we can draw each complete face by
% itself. The format is as follows:

    % Face 1:   X1 Y1
    %           X2 Y2
    %           X3 Y3
    %           X1 Y1
    % - blank row
    % Face 2:   X1 Y1
    %           X2 Y2
    %           X3 Y3
    %           X1 Y1
    % - blank row, etc

%The format can also be read directly from the text file and later saved as
%an Excel for future reference. 
    
%convert from text to MATLAB

    K = textread('c:\Dev-Cpp\geocam\Triangulations\ODE Result.txt');
    S = (size(K,1))/4;

%Create a 'for' loop that plots each triangle, one at a time, by accessing
%the coordinates per triangle and plotting them in a connect-the-dots
%fashion. The 'hold on' line allows for multiple graphs to be placed on one
%figure. 

for i = 0:S-1
    
    %X = [X1 X2 X3 X4] and Y = [Y1 Y2 Y3 Y4] by accessing the elements from
    %K. 
    
    X = [K(i*4 + 1, 1) K(i*4 + 2, 1) K(i*4 + 3, 1) K(i*4 + 4, 1)];
    Y = [K(i*4 + 1, 2) K(i*4 + 2, 2) K(i*4 + 3, 2) K(i*4 + 4, 2)];
    
    plot(X,Y,'k'); %The 'k' makes all lines black. 
    hold on;
    
% To watch the building of the triangulation in action, use this command.
% The number argument is the time interval between each phase of the loop.
% Leaving this command out displays only the end figure. If you need to
% quit the program while it is running, hold Ctrl + C to %cancel. 
   
    pause(0.1);
end

%convert from Excel to MATLAB

%     K = xlsread('c:\Dev-Cpp\geocam\Triangulations\  ');
%     S = (size(K,1) + 1)/5;

% for i = 0:S-1
%     
%     %X = [X1 X2 X3 X4] and Y = [Y1 Y2 Y3 Y4] by accessing the elements from
%     %K. 
%     
%     X = [K(i*5 + 1, 1) K(i*5 + 2, 1) K(i*5 + 3, 1) K(i*5 + 4, 1)];
%     Y = [K(i*5 + 1, 2) K(i*5 + 2, 2) K(i*5 + 3, 2) K(i*5 + 4, 2)];
%     
%     plot(X,Y,'k'); %The 'k' makes all lines black. 
%     hold on;
%    
%     pause(0.01);
% end