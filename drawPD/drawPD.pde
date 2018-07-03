

ArrayList<PVector> points;
float cmin,cmax;
//String curfile;
String [] files;
int curfileID = 0;
boolean loop = true;

void setup(){
   size( 800, 800 ); 
   
   
   files = loadStrings("filelist.txt");
   
   //loop=false;
   //files = new String[]{"running_example.txt"};
   
   loaddata(files[curfileID]);
   
   
}

void loaddata( String filename ){
   String[] lines = loadStrings(filename);
   
   points = new ArrayList<PVector>();
   for( String line : lines ){
      String [] parts = line.split("\\s+");
      points.add( new PVector( Float.parseFloat(parts[0]), Float.parseFloat(parts[1]) ) );
  }
  //cmin = cmax = points.get(0).x;
  cmin = 0;
  cmax = 179;
  for( PVector p : points ){
     cmin = min(cmin,min(p.x,p.y)); 
     cmax = max(cmax,max(p.x,p.y)); 
  }
  println( cmin + " " + cmax );
  
}


void draw(){
  background(255);
  
  
  fill(200);
  stroke(0);
  //strokeWeight(1);
  noStroke();
  
  int size = 15;
  
  if( points.size() < 50 ) size = 20;
  
  int buffer = (size+5);
  
  float x=0,y=0;
  fill(0);
  for( PVector p : points ){
    if( p.x < p.y ){
      x = map( p.x, cmin, cmax, buffer, width-buffer-size*1.5 );
      y = map( p.y, cmin, cmax, height-buffer-size*1.5, buffer );
    }
    else{
      x = map( p.x, cmin, cmax, buffer+size*1.5, width-buffer );
      y = map( p.y, cmin, cmax, height-buffer, buffer+size*1.5 );
    }
    ellipse( x,y, size+4, size+4 );
  }

  noStroke();
  for( PVector p : points ){
    if( p.x < p.y ){
      x = map( p.x, cmin, cmax, buffer, width-buffer-size*1.5 );
      y = map( p.y, cmin, cmax, height-buffer-size*1.5, buffer );
      fill ( 150, 0, 0 );
    }
    else{
      x = map( p.x, cmin, cmax, buffer+size*1.5, width-buffer );
      y = map( p.y, cmin, cmax, height-buffer, buffer+size*1.5 );
      fill ( 0, 0, 150 );
    }
    
    ellipse( x,y, size, size );
  }

  strokeWeight(4);
  stroke(150);
  line( 5,height-5, width-15, 15 );
  
  strokeWeight(8);
  stroke(0);
  line( 5,height-5, width-5, height-5 );
  line( 5,height-5, 5, 5 );

  if( loop ){
    if( curfileID < files.length ){
      String tmp = files[curfileID].replace(".txt",".png");
      saveFrame( "images/" + tmp );
    }
    
    curfileID++;
    if( curfileID >= files.length ) 
      exit();
    else {
      loaddata(files[curfileID]);
    }
  }
}
