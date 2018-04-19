#include <cstdio>
#include <iostream>
#include <fstream>
#include <map>
using namespace std;

map<int,double> index;

int main(){
    char buffer[256];
    char c;
    double d;
    ifstream in("MCoutput5-100.txt");
    for (int i=0;i<24221;i++){
        int x;
        in>>x;
        in>>c;
        in>>c;
        in>>d;
        index[x]=d;
    }
    ifstream answer("myoutput-all.txt");
    double sum=0;
    for (int i=0;i<24221;i++){
        int x;
        answer>>x;
        answer.getline(buffer,100);
        if (i>24190)
        sum+=index[x]*index[x];
        //cout <<index[x];
    }
    cout << sum <<endl;
}
