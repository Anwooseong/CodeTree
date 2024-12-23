import java.util.*;
import java.io.*;

class Point{
    Integer x;
    Integer y;
    public Point(Integer x, Integer y){
        this.x = x;
        this.y = y;
    }
}

class MedusaPoint{
    int x;
    int y;
    Integer prevX;
    Integer prevY;
    int time;

    public MedusaPoint(int x, int y, Integer prevX, Integer prevY, int time){
        this.x = x;
        this.y = y;
        this.prevX = prevX;
        this.prevY = prevY;
        this.time = time;
    }
}

public class Main {

    static BufferedReader bf;
    static StringTokenizer st;
    static int N, M, houseX, houseY, parkX, parkY;
    static int[][] map;
    static MedusaPoint[][] medusaMap;
    static int[][] persons;
    static int[] dx = {-1,1,0,0};
    static int[] dy = {0,0,-1,1};
    static List<int[]> medusaList;

    public static void main(String[] args) throws Exception{
        //도로 -> 0 / 도로 아닌 곳 -> 1
        //조건 초기화
        initFunc();
        moveMedusa();

        for(int i = 0; i < 7; i++){
            int[] medusaPoint = medusaList.get(i);
            System.out.println(medusaPoint[0]+ " " + medusaPoint[1]+ " "+medusaPoint[2]);
        }
    }

    static void moveMedusa(){
        Queue<MedusaPoint> queue = new LinkedList<>();
        MedusaPoint medusaPoint = new MedusaPoint(houseX, houseY, null, null, 0);
        medusaMap[houseX][houseY] = medusaPoint;
        queue.offer(medusaPoint);

        while(!queue.isEmpty()){
            MedusaPoint poll = queue.poll();
            int x = poll.x;
            int y = poll.y;
            if(x == parkX && y == parkY){
                int len = poll.time + 1;
                for(int i = 0; i < len; i++){
                    int[] index = new int[]{poll.x, poll.y, poll.time};
                    System.out.println(poll.time);
                    medusaList.add(index);
                    if(i == len - 1) continue;
                    poll = medusaMap[poll.prevX][poll.prevY];
                }
                return;
            }
            for(int i = 0; i < 4; i++){
                int nextX = x + dx[i];
                int nextY = y + dy[i];
                if(nextX < 0 || nextX >= N || nextY < 0 || nextY >= N) continue;
                if(map[nextX][nextY] == -1 || medusaMap[nextX][nextY] != null) continue;
                medusaPoint = new MedusaPoint(nextX, nextY, x, y, medusaMap[x][y].time + 1);
                medusaMap[nextX][nextY] = medusaPoint;
                queue.offer(medusaPoint);
            }
        }
    }

    static void initFunc() throws Exception{
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(bf.readLine(), " ");

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        map = new int[N][N];
        medusaMap = new MedusaPoint[N][N];
        persons = new int[M][2];
        medusaList = new ArrayList<>();

        st = new StringTokenizer(bf.readLine(), " ");
        houseX = Integer.parseInt(st.nextToken());
        houseY = Integer.parseInt(st.nextToken());
        parkX = Integer.parseInt(st.nextToken());
        parkY = Integer.parseInt(st.nextToken());

        st = new StringTokenizer(bf.readLine(), " ");
        for(int i = 0; i < M; i++){
            int personX = Integer.parseInt(st.nextToken());
            int personY = Integer.parseInt(st.nextToken());
            persons[i][0] = personX;
            persons[i][1] = personY;
        }

        for(int i = 0; i < N; i++){
            st = new StringTokenizer(bf.readLine(), " ");
            for(int j = 0; j < N; j++){
                int value = Integer.parseInt(st.nextToken());
                map[i][j] = value == 1 ? -1 : value;
            }
        }
    }
}