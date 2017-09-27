 import java.text.DecimalFormat;
 import java.util.*;
 
 
 float [] minv;
  float [] maxv;
  
  float [] x,w;
  float [] y;

float plotMinU, plotMaxU,plotMinV, plotMaxV;

float   buffer = 5;
  //float   spaceForAxes = 50;
 // float   ellipseSize = 5;
  int     numTickMarks = 8;
  int     numGuidelines = 4;
DecimalFormat dfX = new DecimalFormat("#.##");
  DecimalFormat dfY = new DecimalFormat("#.##");

void setup() {
  size(1000, 1000);
  noLoop();         // Only run draw() once
   background(255);
   
   
}

void draw() {
 
  plotMinU=width*.2;
  plotMaxU=width*.8;
  plotMinV=height*.2;
  plotMaxV=height*.8;
  

  
 // line(width*.1, height*.9, width*.1, height*.2);    //vertical
 // line(width*.1, height*.9, width*.8, height*.9);    //horizontal
  

  String[] string = loadStrings("first_graph.dmg");
   println("there are " + string.length + " strings");
   
  int rowNo=string.length;
   
  

  w=new float[rowNo];
  x=new float[rowNo];
  y=new float[rowNo];
  
  //String[] wxn = new String[];
   ArrayList<String> edges = new ArrayList<String>();
  
  int idx=0;
  
  edges.add("Digraph G {");
  for ( int rowCount = 0; rowCount<rowNo; rowCount++ ) {
    
      String[] list = split(string[rowCount], ' ');
    if(list[0].equals("v") == true)  {
       
       w[idx] = Float.parseFloat(list[1]);                 
       x[idx] = Float.parseFloat(list[2]);    
    
       println(w[idx]+", "+  x[idx] );     
      
       String joinedwx = list[1]; 
     
       edges.add(joinedwx+ "[label=\" "+list[1] +"(" +list[2]+")\"];");
       idx++; 
     
    }
    else{
      String[] wx = new String[2];
       w[idx] = Float.parseFloat(list[1]);                 
       x[idx] = Float.parseFloat(list[2]);    
    
       println(w[idx]+", "+  x[idx] );     
       wx[0]=list[1];
       wx[1]=list[2];
       String joinedwx = join(wx, " -> "); 
     
       edges.add(joinedwx);
       idx++;     
    }
     
  }
  
  edges.add("}");
  
  String[] array = edges.toArray(new String[edges.size()]);
  
  saveStrings("reebgraph1.gv", array);
   //println(idx);
  
       // find the min and max values for x,y
    minv = new float[]{ Float.MAX_VALUE, Float.MAX_VALUE};
    maxv = new float[]{-Float.MAX_VALUE, -Float.MAX_VALUE}; 
  
    for (int r = 0; r < idx; r++ ) {
      float a0 = x[r];
      float a1 = y[r];
      minv[0] = min(minv[0], a0 );
      minv[1] = min(minv[1], a1 );
      maxv[0] = max(maxv[0], a0 );
      maxv[1] = max(maxv[1], a1 );
    }
    
    println(minv[0]+", "+  maxv[0] );
    println(minv[1]+", "+  maxv[1] );
    
     drawAxes( width*.1, height*.1, width*.9, height*.9, minv, maxv);
    
    for ( int row = 0; row<idx; row++ ) {
       String[] list = split(string[row], ' ');
       float xx = map( x[row], minv[0], maxv[0], plotMinU, plotMaxU );
      float yy = map( y[row], minv[1], maxv[1], plotMinV, plotMaxV );
   fill(0,0,223);
   if(list[0].equals("1") == true)
      ellipse(width*.1+ xx, height*.9-yy-minv[1], 1,1);     
    
  }
  
  
      
  //saveFrame("line-######.png");
}





//steal from Paul Rosen
 
 // internal function to draw axes
  void drawAxes( float plotMinU, float plotMinV, float plotMaxU, float plotMaxV, float [] minv, float [] maxv ) {
    // setup the range of drawing
    float uMin = plotMinU-buffer;
    float vMin = plotMinV+buffer;
    float uMax = plotMaxU;
    float vMax = plotMaxV;


stroke(16);
    strokeWeight(2);
    
    
    fill(0, 102, 153);
    textSize(30);
    text("death", uMin+20,vMin+30);
  text("birth", uMax-80,vMax-10);
  
    // draw axes
    strokeWeight(3);
    stroke(0);
    noFill();
    /* draw x */
    line( uMin, vMax, uMax, vMax );
    /* draw y */
    line( uMin, vMin, uMin, vMax );
    
    /*diagonal*/
    line(uMin, vMax, uMax, vMin);   
    
    /* draw upper x */
    line( uMin, vMin, uMax, vMin );
    /* draw right y */
    line( uMax, vMin, uMax, vMax );

    // Draw Ticks
    strokeWeight(1);
    stroke(0);
    noFill();
    for (int i = 0; i <= numTickMarks; i++ ) {
      float x = map( i, 0, numTickMarks, uMin, uMax );
      float y = map( i, 0, numTickMarks, vMin, vMax );
      line( x, vMax-5, x, vMax+5 );
      line( uMin-5, y, uMin+5, y );
    }
    
    // Draw text
    stroke(0);
    fill(0);
    textSize(16);
    textAlign( RIGHT, CENTER );
    for (int i = 0; i <= numTickMarks; i++ ) {
    float x = map( i, 0, numTickMarks, uMin, uMax );
     float y = map( i, 0, numTickMarks, vMax, vMin );
     //println(minv[0]);
     float xVal = map( i, 0, numTickMarks, minv[0], maxv[0] );
     float yVal = map( i, 0, numTickMarks, minv[1], maxv[1] );
      pushMatrix();
       translate(x, vMax+7);
       rotate( -PI/4 ); 
        text( dfX.format(xVal), 0, 0  );
      popMatrix();
    text( dfY.format(yVal-minv[1]), uMin-7, y );
    } 
  }