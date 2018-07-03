
import java.util.*;

ArrayList<Point> pnts;

float cmin,cmax;
//float vmin,vmax;

int curID = 0;

void setup(){
   size( 800, 800, P3D ); 
   
  loaddata("000-position.txt","000-concentration.txt");
}

long totalPoints = 0;

void loaddata(String posFile, String conFile){
  
  pnts = new ArrayList<Point>();
   String[] lines = loadStrings(posFile);
   
   //println( lines[0] );
   int cnt = Integer.parseInt( lines[0] );
   
   for( int i = 0; i < cnt; i++ ){
      String [] parts = lines[i+1].split("\\s+");
      pnts.add( new Point( Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]) ) );
   }
   
   lines = loadStrings(conFile);
   //concentration = new float[cnt];
   for( int i = 0; i < cnt; i++ ){
      pnts.get(i).concentration = Float.parseFloat(lines[i+1]);
      if( i == 0 ) cmin = cmax = pnts.get(i).concentration;
       cmin = min(cmin,pnts.get(i).concentration);
       cmax = max(cmax,pnts.get(i).concentration);
   }

   /*
   lines = loadStrings("119-velocity.txt");
   vel_mag = new float[cnt];
   for( int i = 0; i < cnt; i++ ){
      String [] parts = lines[i+1].split("\\s+");
      PVector v = new PVector( Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]) );
      vel.add( v );
      vel_mag[i] = v.mag();
   }

   vmin = min(vel_mag);
   vmax = max(vel_mag);
   */  
   totalPoints += pnts.size();
   println(pnts.size() + " " + (totalPoints/(curID+1)) );
}

float crot = 0;


void draw(){
  background(255);
  
  pushMatrix();
  ortho();
  
    strokeWeight(3);
    translate(150, height-250, 0);
  rotateX( PI/2+0+0.1f );
  rotateZ(crot);
  beginShape(LINES);
    stroke(255,0,0); vertex( 0, 0, 0); vertex( 70, 0, 0 );
    stroke(0,255,0); vertex( 0, 0, 0); vertex( 0, -70, 0 );
    stroke(0,0,255); vertex( 0, 0, 0); vertex( 0, 0, 70 );
  endShape();
  popMatrix();
  
  perspective();
  

  translate( 0, 100 );
  translate(width/2, height/2+50);
  scale(30);
  rotateX( PI/2+0+0.1f );
  
  noFill();
  stroke(0);
  strokeWeight(0.1);
  beginShape(LINES);
  float radius = 5.1f;
  float h0 = 10, h1 = -0.25;
  for( int i = 0; i < 40; i++){
    float a0 = map( i,   0, 40, 0, 2*PI );
    float a1 = map( i+1, 0, 40, 0, 2*PI );
    vertex( radius*cos(a0), radius*sin(a0), h0);
    vertex( radius*cos(a1), radius*sin(a1), h0);
    vertex( radius*cos(a0), radius*sin(a0), h1);
    vertex( radius*cos(a1), radius*sin(a1), h1);
    if( i == 0 || i == 20 ){
    }
  }
    float a0 = map( 0.75,  0, 40, 0, 2*PI );
    float a1 = map( 19.25, 0, 40, 0, 2*PI );
    vertex( radius*cos(a0), radius*sin(a0), h0); vertex( radius*cos(a0), radius*sin(a0), h1);
    vertex( radius*cos(a1), radius*sin(a1), h0); vertex( radius*cos(a1), radius*sin(a1), h1);
  endShape();
  
  
  
  rotateZ(crot);
  
  PMatrix3D mat = new PMatrix3D();
  mat.rotateX(PI/2+0+0.1f);
  mat.rotateZ(crot);
  //PVector tmp = new PVector();
  for( int i = 0; i < pnts.size(); i++ ){
    PVector p = pnts.get(i).point;
    pnts.get(i).projZ = mat.multZ( p.x,p.y,p.z );
  }
  
  
  Collections.sort( pnts, new Comparator<Point>(){
    public int compare( Point o1, Point o2 ){
      if( o1.projZ < o2.projZ ) return -1;
      if( o1.projZ > o2.projZ ) return  1;
      return 0;
    }
  });

  fill(0);
  noStroke();
  sphereDetail(5);
  //for( PVector p : points ){
  for( int i = 0; i < pnts.size(); i++ ){
    PVector p = pnts.get(i).point;
    float c = map( pnts.get(i).concentration, cmin,cmax,0,1);
    //float v = map( vel_mag[i], vmin,vmax,0,1);
    
    translate(p.x,p.y,p.z);
    float a = 200*c;
    color rgb = lerpColor( color(255,255,255), color(255,0,0), sqrt(c) );
    
    if( c < 0.5 ){
      rgb = lerpColor( color(148,203,249), color(250,242,188), map( c, 0, 0.3, 0, 1 ) );
    }
    else{
      rgb = lerpColor( color(250,242,188), color(237,192,95), map( c, 0.3, 1, 0, 1 ) );
    }
    //float a = 255*v*v;
    //color rgb = lerpColor( color(255,255,255), color(255,0,0), sqrt(v) );
    fill( red(rgb), green(rgb), blue(rgb), a );
    

    sphere(.075);
    translate(-p.x,-p.y,-p.z);
  }  
  
  crot += 0.05f;
  curID = (curID+1)%121;
  String num = "";
  if( curID < 10 ) num+="0"; if( curID < 100 ) num+="0";
  num += curID;
  loaddata(num+"-position.txt",num+"-concentration.txt");
  
  saveFrame( "images/" + "img" + num + ".png" );
  if( curID == 0 ) exit();
  
}


class Point {
   PVector point;
   float concentration;
   //PVector velocity;
   //float vel_mag;
   Point( float x, float y, float z ){ point = new PVector(x,y,z); }
   float projZ;
}
