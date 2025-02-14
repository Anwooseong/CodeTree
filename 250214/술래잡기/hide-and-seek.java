import java.util.*;
import java.io.*;

public class Main {

    static int n, m, h, k;
    static int[][][] map;
    static boolean[][] treeMap;
    static int[] boss;
    static PriorityQueue<int[]>[][] personMap;
    static int[] personsDirInfo;
    static int answer;
    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};

    public static void main(String[] args) throws Exception {
        init();


        for (int turn = 1; turn <= k; turn++) {
            int i = turn / 25 % 2;
            int[][] selectedMap = map[i];
            simulation(turn, selectedMap);
        }

        System.out.println(answer);

    }

    static void simulation(int turn, int[][] selectedMap) {
        List<int[]> selectedPoint = lessThanThreePersonPoint(turn);
        runPersons(turn, selectedPoint);
        moveBoss(turn, selectedMap);
    }

    static void moveBoss(int turn, int[][] selectedMap) {
        int dir = selectedMap[boss[0]][boss[1]];
        //술래 이동
        boss[0] += dx[dir];
        boss[1] += dy[dir];

        //술래보는 방향
        //거리3만큼 확인(그 자리에 나무 있으면 제외)
        int searchDir = selectedMap[boss[0]][boss[1]];
        int searchX = boss[0];
        int searchY = boss[1];
        int catchPerson = 0;
        for (int length = 1; length <= 3; length++) {
            if (!isInRange(searchX, searchY)) break;

            if (!treeMap[searchX][searchY]) {
                PriorityQueue<int[]> queue = personMap[searchX][searchY];
                int size = queue.size();
                queue.clear();
                catchPerson += size;
            }

            searchX = searchX + dx[searchDir];
            searchY = searchY + dy[searchDir];
        }
        answer += (turn * catchPerson);
    }

    static void runPersons(int turn, List<int[]> selectedPoint) {
        for (int i = 0; i < selectedPoint.size(); i++) {
            int[] point = selectedPoint.get(i);
            int x = point[0];
            int y = point[1];
            PriorityQueue<int[]> queue = personMap[x][y];
            while (!queue.isEmpty()) {
                if (queue.peek()[1] >= turn) {
                    break;
                }

                int[] poll = queue.poll();
                int personNum = poll[0];
                int personTurn = poll[1];
                int dir = personsDirInfo[personNum];
                int nextX = x + dx[dir];
                int nextY = y + dy[dir];
                if (isInRange(nextX, nextY)) { // 격자 벗어나지 않을 떄
                    //술래가 있다면 이동X
                    if (nextX == boss[0] && nextY == boss[1]) {
                        queue.offer(new int[]{personNum, turn});
                        continue;
                    }

                    //술래가 없다면 이동(나무 있어도 가능)
                    personMap[nextX][nextY].offer(new int[]{personNum, turn});

                } else { //격자를 벗어날 때
                    //방향을 반대로하고 술래가 없다면 이동
                    dir = (dir + 2) % 4;
                    nextX = x + dx[dir];
                    nextY = y + dy[dir];
                    personsDirInfo[personNum] = dir;
                    //술래가 있다면 이동X
                    if (nextX == boss[0] && nextY == boss[1]) {
                        queue.offer(new int[]{personNum, turn});
                        continue;
                    }
                    //술래가 없다면 이동(나무 있어도 가능)
                    personMap[nextX][nextY].offer(new int[]{personNum, turn});
                }
            }
        }
    }

    private static List<int[]> lessThanThreePersonPoint(int turn) {
        boolean[][] visited = new boolean[n][n];
        List<int[]> selectedPoint = new ArrayList<>();
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{boss[0], boss[1], 0});
        while (!queue.isEmpty()) {
            int[] poll = queue.poll();
            int x = poll[0];
            int y = poll[1];
            int cnt = poll[2];

            if (cnt > 3) continue;

            if (!personMap[x][y].isEmpty()) {
                int[] peek = personMap[x][y].peek();
                int peekTurn = peek[1];
                if (peekTurn < turn) {
                    selectedPoint.add(new int[]{x, y});
                }
            }

            for (int i = 0; i < 4; i++) {
                int nextX = x + dx[i];
                int nextY = y + dy[i];
                if (isInRange(nextX, nextY) && !visited[nextX][nextY]) {
                    visited[nextX][nextY] = true;
                    queue.offer(new int[]{nextX, nextY, cnt + 1});
                }
            }
        }
        return selectedPoint;
    }

    static void init() throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(bf.readLine(), " ");
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        h = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        boss = new int[2];
        boss[0] = n / 2;
        boss[1] = n / 2;
        answer = 0;

        map = new int[2][n][n]; //술래가 움직일 방향 맵
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    map[i][j][k] = -1;
                }
            }
        }

        personMap = new PriorityQueue[n][n];  // 도망자 위치, 이동했던 턴
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                personMap[i][j] = new PriorityQueue<int[]>((o1, o2) -> o1[1] - o2[1]);
            }
        }
        personsDirInfo = new int[m + 1];// 각 도망자가 움직일 방향
        for (int i = 1; i <= m; i++) {
            st = new StringTokenizer(bf.readLine(), " ");
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            int d = Integer.parseInt(st.nextToken());
            personMap[x][y].offer(new int[]{i, 0});
            personsDirInfo[i] = d;
        }

        treeMap = new boolean[n][n]; //나무 맵
        for (int i = 1; i <= h; i++) {
            st = new StringTokenizer(bf.readLine(), " ");
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            treeMap[x][y] = true;
        }

        int cnt = 1;
        int dir = 2;
        int x = 0;
        int y = 0;

        while (cnt <= n * n) {
            map[0][x][y] = (dir + 2) % 4;
            map[1][x][y] = dir;
            int nextX = x + dx[dir];
            int nextY = y + dy[dir];
            if (!isInRange(nextX, nextY) || map[0][nextX][nextY] != -1) {
                dir = (dir - 1 + 4) % 4;
                nextX = x + dx[dir];
                nextY = y + dy[dir];
                map[1][x][y] = dir;
            }
            x = nextX;
            y = nextY;
            cnt++;
        }
        map[0][0][0] = 2;
        map[0][n / 2][n / 2] = 0;
    }

    static boolean isInRange(int x, int y) {
        return x >= 0 && x < n && y >= 0 && y < n;
    }
}
