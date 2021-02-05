Group Members:-
•	Name: Nikhil Kotian
      UFID: 06999663
•	Name: Ramandeep Singh
      UFID: 8019-7991	

Instruction to compile and run the code:-

1.Download the file
2.Open Cmd/terminal and type cd and file path to go to the directory where the project file is downloaded
3.Run the command “dotnet fsi --langversion:preview project2.fsx numNodes topology algorithm”
   numNodes – Number of actors involved
   topology – one of the four topologies – full, line, 2D, Imp2D
   algorithm – gossip or push-sum
   output – time taken to converge in milliseconds.

Maximum number of Nodes tested

Gossip:
• Line- 10000
• Full- 20000
• 2D- 15000
• Imperfect 2D- 20000 

Push Sum:
• Line- 1000
• Full- 1000
• 2D- 1000
• Imperfect 2D- 1000 

