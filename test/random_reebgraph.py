# -*- coding: utf-8 -*-
"""
Created on Tue Feb 13 23:06:40 2018

@author: steve
"""

import networkx as nx
import random
import math
def write_graph_to_hd(filepath,graph):
     f = open(filepath,'w')
     for node in graph.nodes():
         line=''
                           
         line="v"+' '+str(node)+' '+str((node+1)*math.log((node+1)))
        
         
         f.write(line+'\n')

     
     for edge in graph.edges():
         line=''
                           
         line="e"+' '+str(edge[0])+' '+str(edge[1])
        
         
         f.write(line+'\n')
     f.close()     
     
     print("done!")
     return 1   

class generate_reeb_graph():
     def __init__(self):
         self.G = nx.MultiGraph()
         self.G.add_node(0)

        
     def create_fork(self):
         H=nx.MultiGraph()
         m=max(self.G.nodes())+1
         H.add_node(m)
         H.add_node(m+1)
         
         H.add_node(m+2)
         H.add_node(m+3)
         
         H.add_edge(m,m+3)
         H.add_edge(m+1,m+3)
         H.add_edge(m+2,m+3)
         return H
     
     def valency_one(self,graph):
         lst=[]
         for node in graph.nodes():
             if len(graph.neighbors(node))==1:
                 lst.append(node)
         return lst        
             
         
     def glue_fork(self,tree=None): # when the tree is not None the function will generate a contour tree.
         
         lst2=self.valency_one(self.G)
         
         # create fork

          
         
         
         choice=random.randint(0, 1)
         
         
         if(len(self.G.nodes()))<2:
             node=self.G.nodes()[0]
             m=max(self.G.nodes())+1
             self.G.add_node(m)
             self.G.add_node(m+1) # center
             
             self.G.add_node(m+2)
             self.G.add_node(m+3)
             
             self.G.add_edge(m,m+1)
             self.G.add_edge(m+2,m+1)
             self.G.add_edge(m+3,m+1)             
             self.G.add_edge(node,m)
             
             return self.G
         else:


              
           
             
             if tree==None and len(lst2)>2 and choice==1:    
      
                 m=max(self.G.nodes())+1
                 self.G.add_node(m)
                 
                 self.G.add_node(m+1) 
                 
                 self.G.add_node(m+2) # center
                 
                 self.G.add_node(m+3)
                 
                 self.G.add_edge(m,m+2)
                 self.G.add_edge(m+1,m+2)
                 self.G.add_edge(m+3,m+2) 
                 
                 node1=random.randint(0, len(lst2)-1)
                 node2=random.randint(0, len(lst2)-1)
                 
                 while node1==node2:
                     
                     node1=random.randint(0, len(lst2)-1)
                     node2=random.randint(0, len(lst2)-1)
                 self.G.add_edge(m,lst2[node1])
                 self.G.add_edge(m+1,lst2[node2])
      
                 return self.G
             else:
            
                 #this gives a tree when used on its own
                 m=max(self.G.nodes())+1
                 self.G.add_node(m)
                 self.G.add_node(m+1)# center
                 
                 self.G.add_node(m+2)
                 self.G.add_node(m+3)
                 
                 self.G.add_edge(m,m+1)
                 self.G.add_edge(m+2,m+1)
                 self.G.add_edge(m+3,m+1)
                 
                
                 node1=random.randint(0, len(lst2)-1)
                 self.G.add_edge(m,lst2[node1])

                 return self.G           
         
     def generate_random_graph(self,itr):
         for i in range(0,itr):
             self.glue_fork()
             
         for node in self.G.nodes():
              if (len(self.G.neighbors(node)))>=4:# graph is not allowed to have valency larger than 3
                  print("error, the graph has valency more than 3")
                  
          # delete 2-valency nodes
#         k=0 
##    
#         while k==0:
##    
#             for i in range(0,len(self.G.nodes())):
#                 #print("here------------------")
#                 node=self.G.nodes()[i]
#                 #M=self.G.copy()
#                 if (len(self.G.neighbors(node)))==2:
#                     node1=self.G.neighbors(node)[0]
#                     edge=(node, node1)
#                     self.G = nx.contracted_edge(self.G, edge,self_loops=False)
#                     #print(len(M.edges()))
#                     #print(len(self.G.edges()))
#                     #self.G=M
#                     break
#                 if i==len(self.G.nodes())-1:
#                     k=1
                     
                 
             
import matplotlib.pyplot as plt

        
         
rg=generate_reeb_graph()

rg.generate_random_graph(10000)         

#nx.draw(rg.G)  # networkx draw()
#plt.draw()  # pyplot draw()              
 

write_graph_to_hd("C:/1/randomgraphs/10000_graph_iterations.txt",rg.G)        
         
      
         
         
         
 

 