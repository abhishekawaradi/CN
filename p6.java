import java.util.Scanner;
class dist_vec
{
public static void main(String args[])
{
int dmat[][];
int dist[][];
int via[][];
int n = 0;
int i = 0;
int j = 0;
int k = 0;
int count = 0;
Scanner in = new Scanner(System.in);
System.out.println("Enter the number of nodes:");
n = in.nextInt();
dmat = new int[n][n];
dist = new int[n][n];
via = new int[n][n];
System.out.println("Enter the cost matrix:");
for(i = 0; i < n; i++)
{
for(j = 0; j < n; j++)
{
dmat[i][j] = in.nextInt();
if(i == j)
{
dmat[i][j] = 0;
}
dist[i][j] = dmat[i][j];
via[i][j] = j;
}
}
do
{
count = 0;
for(i = 0; i < n; i++)
{
for(j = 0; j < n; j++)
{
for(k = 0; k < n; k++)
{
if(dist[i][j] > dmat[i][k] + dist[k][j])
{
dist[i][j] = dmat[i][k] + dist[k][j];
via[i][j] = k;
count++;
}
}
}
}
} while(count != 0);
for(i = 0; i < n; i++)
{
System.out.println("\nState value for router " + i + " is:");
for(j = 0; j < n; j++)
{
System.out.println( "To " + j + " - Via " + via[i][j] + " distance is " + dist[i][j] );
}
}
in.close();
}
}
