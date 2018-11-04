
// Test.cpp
//
// This is a direct port of the C version of the RTree test program.
//

#include <iostream>  
#include "RTree2.h"
#include <time.h>  
#include<cmath>
#include<fstream>
#include <sstream> 
#include<ctime> 
#include<cstring>
using namespace std;
///加了状态 
//sta[100]={0};
int X1[2505][3505],Y1[2505][3505];//不确定是否会溢出 
//vector<int> X1[2505],Y1[2505];
//X1[0].push_back(s1);
//X1[0].push_back(s1);
//X1[0][1

int sum=0;
typedef int ValueType;

struct Rect
{
  Rect()  {}

  Rect(int a_minX, int a_minY, int a_maxX, int a_maxY)
  {
    min[0] = a_minX;
    min[1] = a_minY;

    max[0] = a_maxX;
    max[1] = a_maxY;
  }


  int min[2];
  int max[2];
};




struct Rect rects[] =
{
    // xmin, ymin, xmax, ymax (for 2 dimensional RTree)
 /* Rect(15, 15, 30, 27),
  Rect(38, 135, 59, 166),
  Rect(107, 1, 139, 22),
  Rect(111,41,132,63),//
  Rect(7,152,29,178),//
  Rect(50, 50, 62, 62), // xmin, ymin, xmax, ymax (for 2 dimensional RTree)

*/
};

//int nrects = sizeof(rects) / sizeof(rects[0]);




//Rect search_rect(6, 4, 10, 6); // search will find above rects that this one overlaps
int mm[10000]={0};
int total=0;
int rectnum[2505]={0};

bool MySearchCallback(ValueType id, void* arg)
{

  cout <<"相交的子区域id: "<<"Hit data rect " << id << "\n";
  mm[total]=id;total++;
// if(state[id]==0)
  //{
  //sum++;state[id]=1;mm[cnt]=id;cnt++;
//	}

  
  return true; // keep going
}


void clear_file(const char*	name)
{
	FILE*fp;
	fp=fopen(name,"w");
	if(fp)
	fclose(fp);
 } 
 //int LL=10000;
 
 
 
int  dx=270000/50,dy=2500000/50;

int locid(int X1,int Y1)
{
	int k1=int(X1/dx);int k2=int(Y1/dy);
	return 50*k2+k1;
	
}
 

int main()
{
	clock_t start2,end2;  
	 
	freopen("out1.txt","w",stdout); 
//	rects[0]=Rect(307, 1, 329, 132);
int nrects=2500;//50*50


	 
//	int cc=16;//每个框内包含的task数 
	int T=10000;//搜索次数  
//	clear_file("data.txt");
	clear_file("location.txt");
	
//	ofstream fout;
//	fout.open("dataset_ubicomp2013_checkins.txt");
	
	
	srand((unsigned)time(NULL));  
  typedef RTree<ValueType, int, 2, float> MyTree;
  MyTree tree;

  int i, nhits;
 // cout << "nrects = " << nrects << "\n";

  
/*  int X1[2505][500],Y1[2505][500];//////////////!!!!!!!!!!!!!!!!!!!!!!!!!!!错了错了应该是二维的 
  for(int ii=0;ii<nrects;ii++)
  {
  	int p1=rects[ii].min[0],q1=rects[ii].max[0];
  	int p2=rects[ii].min[1],q2=rects[ii].max[1];
  	for(int jj=0;jj<cc;jj++)////////////////////////X1[][],Y1[][]是在矩形中随机生成的模拟task点，每个矩形里放6个 
  	{
  		X1[ii][jj]=(rand() % (q1-p1))+ p1; 
  		Y1[ii][jj]=(rand() % (q2-p2))+ p2; //？？？？？？？？？？？？？？？？？？？？？？？考虑下到底啥情况 
  		fout<<X1[ii][jj]<<"  "<<Y1[ii][jj]<<'\n';
	
	}
  	
  	
  }
*/ 


//记录每个区域已经填充的数量 
ifstream infile;
//ifstream ifile("dataset_ubicomp2013_checkins.txt");
infile.open("dataset_ubicomp2013_checkins.txt", ios::in); 
while(!infile.eof())
{
//ifile.getline(line,256);
//puts(line);
//istringstream iss(line);


int s1,s2,rectid; //rectid 记录分配进的区域的id 
 infile>>s1>>s2;
 rectid=locid(s1,s2);
int h=rectnum[rectid];
   X1[rectid][h]=s1;
   Y1[rectid][h]=s2;
   rectnum[rectid]++;
 //  if(s1==15414 && s2==18177) {cout<<s1<<' '<<s2<<' '<<rectid<<' '<<h<<' '<<X1[rectid][h]<<endl;}
  // if(rectid == 0 && X1[rectid][h]>=5400) exit(1); 
   //if(rectid == 0) cout<<rectnum[rectid]<<' ' << X1[rectid][rectnum[rectid]-1]<<endl;
}

//for (int ii=0; ii < 1450; ii ++) {
//	cout<<X1[0][ii]<<' '<<Y1[0][ii]<<endl;
//}
infile.close();


//接下来要将2500rect[]赋值写入struct类型中 


for(int g=0;g<50;g++)//g代表列，r 代表行 
{
	for(int r=0;r<50;r++)
	{
		int c1=r*dx,c2=g*dy,c3=(r+1)*dx,c4=(g+1)*dy;
		
		rects[g*50+r]=Rect(c1,c2,c3,c4);
	}

}
for(int i4=0; i4<nrects; i4++)
  {
    tree.Insert(rects[i4].min, rects[i4].max, i4); // Note, all values including zero are fine in this version
  }
	ofstream fout;
//	fout.open("dataset_ubicomp2013_checkins.txt");
	fout.open("location.txt");
	
  double dis;
  bool haha[30000]={0};
   //start计时 
   start2=clock(); 
for(int i3=0;i3<T;i3++)
{
	
	//bool state[5000]={0};
	 memset(mm,0,sizeof(mm));
   total=0;//?????????????????????????????????????????????????????????????????????????????????????????????
	int k1=rand()%(100000-80000)+80000;//////////////////////////////////搜索圆的位置坐标 
	int  k2=rand()%(1000000-80000)+80000;
	int k3=6643+500*9;
	fout<<k1+k3<<"  "<<k2+k3<<"  "<<k3<<"\n"; 
//	int k4=rand()%50;
	cout<<"随机产生的第"<<i3+1<<"个worker的搜索圆心为"<<"("<<k1+k3<<","<<k2+k3<<")"<<"搜索半径为"<<k3<<endl<<endl;
	Rect search_rect(k1,k2,k1+2*k3,k2+2*k3);/////////////////////////////////////////输入搜索的矩形 
	nhits = tree.Search(search_rect.min, search_rect.max, MySearchCallback, NULL);

  cout << "相交的子区域个数"<<":   "<<"Search resulted in " << nhits << " hits\n";
  
  double w1=k1+k3,w2=k2+k3;
 // cout<<"nhits="<<nhits<<"++++++++++++++++++++++"<<endl;
 // cout<<"total="<<total<<"++++++++++++++++++++++"<<endl;
  for(int i2=0;i2<total;i2++)
  {
  //	cout<<"total:  "<<total<<"            "<<"nhits:  "<<nhits<<"\n";
  //	for(int kk=0;kk<total;kk++)
  //	{
  //		cout<<"序号："<<mm[kk]<<"    ";
//	  }
	  cout<<endl;
  	int u=mm[i2];
  	
 	//if(state[u]==1)
 //	continue;
	 
	  //state[u]=1;
	
  //	cout<<"i2:"<<i2<<"+++++"<<endl;
  	cout<<"子区域的id="<<u<<":  "<<endl;
  	int yy=rectnum[u];
  	cout<<"^^^^^^^^^^^^^^^^^^^^^^^^^^^^^6^^^^^^^^^^^^^^^^"<<endl;
 /* 	for(int hh=0;hh<yy;hh++)
  	{
  		//if(u==0 && X1[u][hh] > 5400)
		  cout<<u<<"    11111    "<<X1[u][hh]<<"   "<<Y1[u][hh]<<endl;
	  }
*/	  cout<<"^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"<<endl;
  	
  	for(int j3=0;j3<yy;j3++)
  	{
  		
  		
  		
  		dis=sqrt((w1-X1[u][j3])*(w1-X1[u][j3])+(w2-Y1[u][j3])*(w2-Y1[u][j3]));
  	//	cout<<"???"<<X1[u][j3]<<"   "<<Y1[u][j3]<<"   "<<dis<<endl;
  		if(dis<=k3)
  		{
  			haha[i3]=1; 
  			cout<<"the x of task is :"<<X1[u][j3]<<"  ,"<<"the y of task is :"<<Y1[u][j3]<<endl;
		  }
  		
	  }
  
  
  
  
}
//exit(1);

if(haha[i3]==0)
  {
  	cout<<"没有满足条件的task"<<endl;
  }
  
  cout<<"******************************************************************"<<endl<<endl;
}
 end2=clock();  
      
    cout<<endl;cout<<"end: "<<end2<<"  start: "<<start2<<endl;
   cout<<"搜索"<<T<<"次的总时间为:  "<<end2-start2<< "/" << CLOCKS_PER_SEC  << " (s) "<< endl;
	fout<<flush;
	fout.close();
//}


//}


 /*nhits = tree.Search(search_rect.min, search_rect.max, MySearchCallback, NULL);

  cout << "Search resulted in " << nhits << " hits\n";
*/
  // Iterator test
  int itIndex = 0;
  MyTree::Iterator it;
  for( tree.GetFirst(it);
       !tree.IsNull(it);
       tree.GetNext(it) )
  {
    int value = tree.GetAt(it);

    int boundsMin[2] = {0,0};
    int boundsMax[2] = {0,0};
    it.GetBounds(boundsMin, boundsMax);
    cout << "it[" << itIndex++ << "] " << value << " = (" << boundsMin[0] << "," << boundsMin[1] << "," << boundsMax[0] << "," << boundsMax[1] << ")\n";
  }

  // Iterator test, alternate syntax
/*  itIndex = 0;
  tree.GetFirst(it);
  while( !it.IsNull() )
  {
    int value = *it;
    ++it;
    cout << "it[" << itIndex++ << "] " << value << "\n";
  }
 // cout<<"the total num is :"<<sum<<endl;
  //cout<<"the id is : ";
 */
  ////////遍历所有的task
 
  return 0;

  // Output:
  //
  // nrects = 4
  // Hit data rect 1
  // Hit data rect 2
  // Search resulted in 2 hits
  // it[0] 0 = (0,0,2,2)
  // it[1] 1 = (5,5,7,7)
  // it[2] 2 = (8,5,9,6)
  // it[3] 3 = (7,1,9,2)
  // it[0] 0
  // it[1] 1
  // it[2] 2
  // it[3] 3
}

