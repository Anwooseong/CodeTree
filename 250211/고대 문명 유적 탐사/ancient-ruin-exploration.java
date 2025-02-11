import java.util.*;
import java.io.*;

public class Main {

    static int K, M;
    static int[][] map;
    static Queue<Integer> queue;
    static final int ONE = 90, TWO = 180, THREE = 270;
    static List<Integer> answer;
    static int[][] gainMap;
    static int[] dx = {0, 0, -1, 1};
    static int[] dy = {-1, 1, 0, 0};


    public static void main(String[] args) throws Exception {
        init();

        for (int time = 1; time <= K; time++) {
            // [1] 탐사진행
            int maxCnt = step_1();

            // 유물이 없는 경우 턴 즉시 종료
            if (maxCnt == 0) break;

            // [2] 연쇄 획득
            int cnt = 0;
            map = gainMap;
            while (true) {
                int tmp = countClear(map, 1);
                if (tmp == 0) break; //연쇄 획득 종료 -> 다음 턴

                cnt += tmp;

                // map의 0값인 부분 리스트에서 순서대로 추가
                for (int j = 0; j < 5; j++) {
                    for (int i = 4; i >= 0; i--) {
                        if (map[i][j] == 0) {
                            if (!queue.isEmpty()) {
                                map[i][j] = queue.poll();
                            }
                        }
                    }
                }
            }
            answer.add(cnt);
        }
        for (Integer integer : answer) {
            System.out.print(integer+" ");
        }
    }

    static int step_1() {
        int maxCnt = 0;
        for (int rot = 1; rot < 4; rot++) {  // 회전수 > 열 > 행(작은순)
            for (int sj = 0; sj < 3; sj++) {
                for (int si = 0; si < 3; si++) {
                    // rot 횟수 만큼 90도 시계 방향 회전
                    int[][] copyMap = copyMap(map);
                    for (int i = 0; i < rot; i++) {
                        copyMap = rotate(copyMap, si, sj);
                    }

                    //유물 갯수 카웉트
                    int tmp = countClear(copyMap, 0);
                    if (maxCnt < tmp) {
                        maxCnt = tmp;
                        gainMap = copyMap;
                    }
                }
            }
        }
        return maxCnt;
    }

    static int countClear(int[][] copyMap, int flag) {
        // flag = 1 인 경우 3개 이상값들을 0으로 변환
        boolean[][] visited = new boolean[5][5];
        int cnt = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) { //미방문인 경우 같은 값이면 Fill
                if (!visited[i][j]) {
                    int tmp = bfs(copyMap, visited, i, j, flag);
                    cnt += tmp;
                }
            }
        }
        return cnt;
    }

    static int bfs(int[][] copyMap, boolean[][] visited, int i, int j, int flag) {
        Queue<int[]> queue = new LinkedList<>();
        Queue<int[]> removeQueue = new LinkedList<>();
        queue.offer(new int[]{i, j});
        removeQueue.offer(new int[]{i, j});
        visited[i][j] = true;

        int cnt = 1;
        int originNum = copyMap[i][j];

        while (!queue.isEmpty()) {
            int[] poll = queue.poll();
            for (int dir = 0; dir < 4; dir++) {
                int nextX = poll[0] + dx[dir];
                int nextY = poll[1] + dy[dir];
                if (nextX < 0 || nextX >= 5 || nextY < 0 || nextY >= 5) continue;
                if (visited[nextX][nextY]) continue;
                if (copyMap[nextX][nextY] != originNum) continue;

                visited[nextX][nextY] = true;
                queue.offer(new int[]{nextX, nextY});
                removeQueue.offer(new int[]{nextX, nextY});
                cnt++;
            }
        }

        if (cnt >= 3) { //flag = 1 이면 유물 0으로 반환
            if (flag == 1) {
                while (!removeQueue.isEmpty()) {
                    int[] poll = removeQueue.poll();
                    copyMap[poll[0]][poll[1]] = 0;
                }
            }
            return cnt;
        }

        return 0;
    }

    static int[][] rotate(int[][] copyMap, int si, int sj) {
        int[][] rotateMap = copyMap(copyMap);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                rotateMap[si + i][sj + j] = copyMap[si + 3 - j - 1][sj + i];
            }
        }
        return rotateMap;
    }

    static int[][] copyMap(int[][] map) {
        int[][] copyMap = new int[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                copyMap[i][j] = map[i][j];
            }
        }
        return copyMap;
    }

    static void init() throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(bf.readLine(), " ");
        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        answer = new ArrayList<>();
        map = new int[5][5];

        for (int i = 0; i < 5; i++) {
            st = new StringTokenizer(bf.readLine(), " ");
            for (int j = 0; j < 5; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        queue = new LinkedList<>();
        st = new StringTokenizer(bf.readLine(), " ");
        for (int i = 0; i < M; i++) {
            queue.offer(Integer.parseInt(st.nextToken()));
        }
    }

}