import java.util.*;
import java.io.*;

public class Main {

    static int n, m, answer, monsterCnt;
    static int[][] map;
    static int[][] attackInfo;
    static int[][] dirMap, reverseDirMap;
    static int[] tower;
    static int[] dx = {0, 1, 0, -1};
    static int[] dy = {1, 0, -1, 0};

    public static void main(String[] args) throws Exception {
        init();

        for (int round = 1; round <= m; round++) {
            simulation(round - 1);
        }

        System.out.println(answer);
    }

    static void simulation(int round) {
        // 1. 포탑 공격
        step_1(round);

        // 2. 포탑 공격후 몬스터들 앞으로 당기기
        step_2();

        // 3. 4번 이상 반복된 몬스터들 제거후 앞으로 당기기
        // 3-1. 제거된 이후 또 4번 이상있으면 또 제거
        boolean result = step_3();
        while (result) {
            result = step_3();
        }

        // 4. 삭제가 끝난 다음 몬스터들을 차례로 배열화해서 (총갯수, 숫자의 크기)로 다시 배열을 만들어서 새로운 맵 생성
        step_4();

    }

    static void step_4() {
        Queue<Integer> queue = new LinkedList<>();
        Queue<Integer> newQueue = new LinkedList<>();
        int x = tower[0];
        int y = tower[1] - 1;
        int[][] newMap = new int[n][n];
        while (map[x][y] != 0) {
            queue.offer(map[x][y]);
            int dir = reverseDirMap[x][y];
            x = x + dx[dir];
            y = y + dy[dir];
        }

        if (!queue.isEmpty()) {
            int cnt = 1;
            int num = queue.poll();
            while (!queue.isEmpty()) {
                int poll = queue.poll();
                if (num == poll) {
                    cnt++;
                } else if (num != poll) {
                    newQueue.offer(cnt);
                    newQueue.offer(num);
                    cnt = 1;
                    num = poll;
                }
            }
            newQueue.offer(cnt);
            newQueue.offer(num);
        }

        x = tower[0];
        y = tower[1] - 1;
        while (!newQueue.isEmpty()) {
            int poll = newQueue.poll();
            newMap[x][y] = poll;
            int dir = reverseDirMap[x][y];
            x = x + dx[dir];
            y = y + dy[dir];
        }

        map = newMap;
    }

    static boolean step_3() {
        int x = tower[0];
        int y = tower[1] - 1;
        Queue<int[]> queue = new LinkedList<>();
        int num = 0;
        int cnt = 0;
        boolean isFlag = false;
        int score = 0;
        while (map[x][y] != 0) {
            if (num != map[x][y]) { //다른 몬스터가 나올때
                if (queue.size() >= 4) { //몬스터의 종류가 4번 이상 반복이면 삭제
                    isFlag = true;
                    int size = queue.size();
                    int pollNum = 0;
                    while (!queue.isEmpty()) {
                        int[] poll = queue.poll();
                        pollNum = map[poll[0]][poll[1]];
                        map[poll[0]][poll[1]] = 0;
                        cnt++;
                    }
                    score = score + (pollNum * size);
                }
                queue.clear();
                num = map[x][y];
                queue.offer(new int[]{x, y});
            } else if (num == map[x][y]) {
                queue.offer(new int[]{x, y});
            }

            int dir = reverseDirMap[x][y];
            x = x + dx[dir];
            y = y + dy[dir];
        }

        monsterCnt -= cnt; //삭제된 만큼 몬스터 갯수 줄이기
        if (score > 1) answer += score;
        forwardMonsters();
        return isFlag;
    }

    static void step_2() {
        forwardMonsters();
    }

    static void forwardMonsters() {
        int x = tower[0];
        int y = tower[1] - 1;
        Queue<int[]> zeroQueue = new LinkedList<>();
        int cnt = 0;
        while (monsterCnt != cnt) {
            if (map[x][y] != 0) {
                // (x,y)위치에 몬스터가 있을때
                if (!zeroQueue.isEmpty()) {
                    //해당 위치에 몬스터가 있는데 제로큐에 0이 존재할때
                    int[] poll = zeroQueue.poll();
                    int tmp = map[x][y];
                    map[poll[0]][poll[1]] = tmp;
                    map[x][y] = 0;
                    zeroQueue.offer(new int[]{x, y});
                }
                cnt++;
            } else {
                //해당 위치가 0일때
                zeroQueue.offer(new int[]{x, y});
            }
            int dir = reverseDirMap[x][y];
            x = x + dx[dir];
            y = y + dy[dir];
        }
    }

    static void step_1(int round) {
        int attackDir = attackInfo[round][0];
        int attackLength = attackInfo[round][1];
        int length = 0;
        int x = tower[0];
        int y = tower[1];
        int score = 1;
        boolean isAttack = false;
        while (length <= attackLength) {
            length++;
            x = x + dx[attackDir];
            y = y + dy[attackDir];
            if (isInRange(x, y)) {
                if (map[x][y] != 0) {
                    isAttack = true;
                    monsterCnt--;
                    score *= map[x][y];
                    map[x][y] = 0;
                }
            }
        }
//        System.out.println("score = " + score);

        if (isAttack) answer += score;
    }

    private static void init() throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(bf.readLine(), " ");
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        monsterCnt = 0;
        answer = 0;
        tower = new int[2];
        tower[0] = n / 2;
        tower[1] = n / 2;

        map = new int[n][n];
        dirMap = new int[n][n];
        reverseDirMap = new int[n][n];
        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(bf.readLine(), " ");
            for (int j = 0; j < n; j++) {
                dirMap[i][j] = -1;
                reverseDirMap[i][j] = -1;
                map[i][j] = Integer.parseInt(st.nextToken());
                if (map[i][j] != 0) monsterCnt++;
            }
        }

        attackInfo = new int[m][2];
        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(bf.readLine(), " ");
            int d = Integer.parseInt(st.nextToken());
            int p = Integer.parseInt(st.nextToken());
            attackInfo[i][0] = d; // 공격 방향
            attackInfo[i][1] = p; // 공격칸 수
        }

        // 앞으로 당기기위한 방향 맵
        int x = 0;
        int y = 0;
        int dir = 0;
        int reverseDir = 2;
        for (int i = 1; i <= n * n; i++) {
            dirMap[x][y] = dir;
            reverseDirMap[x][y] = reverseDir;
            int nextX = x + dx[dir];
            int nextY = y + dy[dir];
            if (!isInRange(nextX, nextY) || dirMap[nextX][nextY] != -1) {
                dir = (dir + 1) % 4;
                dirMap[x][y] = dir;
                reverseDirMap[x][y] = reverseDir;
                reverseDir = (reverseDir + 1) % 4;
            }
            x = x + dx[dir];
            y = y + dy[dir];
        }

    }

    static boolean isInRange(int x, int y) {
        return x >= 0 && x < n && y >= 0 && y < n;
    }
}