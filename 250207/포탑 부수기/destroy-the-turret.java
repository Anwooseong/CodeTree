import java.util.*;
import java.io.*;

class Point {
    int pastX;
    int pastY;

    public Point(int pastX, int pastY) {
        this.pastX = pastX;
        this.pastY = pastY;
    }
}

public class Main {

    static int N, M, K;
    static int[][] arr, turn;
    static int minPower, recentAttacker, startX, startY, maxPower, olderAttacker, targetX, targetY;
    static Point[][] findLaser;
    static int[] dx = {0, 1, 0, -1};
    static int[] dy = {1, 0, -1, 0}; //우하좌상 순


    public static void main(String[] args) throws Exception {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(bf.readLine(), " ");
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        arr = new int[N][M];
        turn = new int[N][M];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(bf.readLine(), " ");
            for (int j = 0; j < M; j++) {
                arr[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        for (int time = 1; time <= K; time++) {
            // 공격 포탑과 공격 당할 포탑 선정
            selectedTower();

            // 공격
            // 레이저 공격
            int power = arr[startX][startY] + (N + M);
            boolean isFinishLaser = isFinishLaser();
            int[][] copyArr = new int[N][M];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    copyArr[i][j] = arr[i][j];
                }
            }
            if (isFinishLaser) {
                int x = targetX, y = targetY;
                while (true) {
                    if (x == startX && y == startY) break;
                    if (x == targetX && y == targetY) {
                        arr[x][y] = arr[x][y] - power <= 0 ? 0 : arr[x][y] - power;
                    } else {
                        arr[x][y] = arr[x][y] - power / 2 <= 0 ? 0 : arr[x][y] - power / 2;
                    }
                    Point pastPoint = findLaser[x][y];
                    x = pastPoint.pastX;
                    y = pastPoint.pastY;
                }
            } else {
                // 포탄 공격
                arr[targetX][targetY] = arr[targetX][targetY] - power <= 0 ? 0 : arr[targetX][targetY] - power;
                int[] eightDirX = {-1, -1, -1, 0, 1, 1, 1, 0};
                int[] eightDirY = {-1, 0, 1, 1, 1, 0, -1, -1};
                for (int i = 0; i < 8; i++) {
                    int nextX = (targetX + eightDirX[i] + N) % N; // 경계 처리
                    int nextY = (targetY + eightDirY[i] + M) % M; // 경계 처리
                    if (arr[nextX][nextY] == 0) continue;
                    if (nextX == startX && nextY == startY) continue;
                    arr[nextX][nextY] = arr[nextX][nextY] - power / 2 <= 0 ? 0 : arr[nextX][nextY] - power / 2;
                }
            }
            turn[startX][startY] = time;
            arr[startX][startY] = power;

            //포탑 정비
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    if (i == startX && j == startY) continue;
                    if (arr[i][j] == 0) continue;
                    if (arr[i][j] == copyArr[i][j]) arr[i][j]++;
                }
            }
        }

        int answer = Integer.MIN_VALUE;
        for (int[] ints : arr) {
            for (int anInt : ints) {
                answer = Math.max(anInt, answer);
            }
        }
        System.out.println(answer);
    }

    static boolean isFinishLaser() {
        findLaser = new Point[N][M];
        Queue<int[]> queue = new LinkedList<>();
        boolean isFinishLaser = false;
        queue.offer(new int[]{startX, startY});
        findLaser[startX][startY] = new Point(-1, -1); // 시작 점 방문 처리

        while (!queue.isEmpty()) {
            int[] poll = queue.poll();
            int x = poll[0];
            int y = poll[1];
            if (x == targetX && y == targetY) {
                isFinishLaser = true;
                break;
            }

            for (int i = 0; i < 4; i++) {
                int nextX = (x + dx[i] + N) % N; // 경계 처리
                int nextY = (y + dy[i] + M) % M; // 경계 처리

                if (arr[nextX][nextY] == 0 || findLaser[nextX][nextY] != null) continue;
                queue.offer(new int[]{nextX, nextY});
                findLaser[nextX][nextY] = new Point(x, y);
            }
        }
        return isFinishLaser;
    }


    static boolean isInRange(int x, int y) {
        return x >= 0 && x < N && y >= 0 && y < M;
    }

    static void selectedTower() {
        // 공격자
        minPower = Integer.MAX_VALUE;
        recentAttacker = 0;
        startX = 0; //공격자
        startY = 0; //공격자


        // 공격 당할 포탑
        maxPower = Integer.MIN_VALUE;
        olderAttacker = Integer.MAX_VALUE;
        targetX = 0; //공격당할포탑
        targetY = 0; //공격당할포탑

        // 공격자와 공격 당할 포탑 선정
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (arr[i][j] <= 0) continue;

                // 젤 공격력 낮음
                if (minPower > arr[i][j]) {
                    minPower = arr[i][j];
                    recentAttacker = turn[i][j];
                    startX = i;
                    startY = j;
                } else if (minPower == arr[i][j]) {
                    //가장 최근에 공격한 포탑
                    if (recentAttacker < turn[i][j]) {
                        minPower = arr[i][j];
                        recentAttacker = turn[i][j];
                        startX = i;
                        startY = j;
                    }
                    // 행렬 총합이 가장 큰
                    else if (startX + startY < i + j) {
                        minPower = arr[i][j];
                        recentAttacker = turn[i][j];
                        startX = i;
                        startY = j;
                    }
                    // 열이 가장 큰
                    else if (startY < j) {
                        minPower = arr[i][j];
                        recentAttacker = turn[i][j];
                        startX = i;
                        startY = j;
                    }
                }

                // 가장 강한 포탑
                if (maxPower < arr[i][j]) {
                    maxPower = arr[i][j];
                    olderAttacker = turn[i][j];
                    targetX = i;
                    targetY = j;
                } else if (maxPower == arr[i][j]) {
                    // 공격한지 가장 오래된 포탑
                    if (olderAttacker < turn[i][j]) {
                        maxPower = arr[i][j];
                        olderAttacker = turn[i][j];
                        targetX = i;
                        targetY = j;
                    }
                    // 행렬합이 가장 작은 포탑
                    else if (targetX + targetY > i + j) {
                        maxPower = arr[i][j];
                        olderAttacker = turn[i][j];
                        targetX = i;
                        targetY = j;
                    }
                    // 열 값이 가장 작은 포탑
                    else if (targetY > j) {
                        maxPower = arr[i][j];
                        olderAttacker = turn[i][j];
                        targetX = i;
                        targetY = j;
                    }
                }
            }
        }
    }
}