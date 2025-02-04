import java.util.*;
import java.io.*;

public class Main {

    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};

    static int R, C, K, answer;
    static int[][] map, monsters, exitMap;

    public static void main(String[] args) throws Exception{
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(bf.readLine(), " ");
        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        answer = 0;

        initMap();
        monsters = new int[K + 1][2];

        for(int i = 1; i <= K; i++){
            st = new StringTokenizer(bf.readLine(), " ");
            monsters[i][0] = Integer.parseInt(st.nextToken());
            monsters[i][1] = Integer.parseInt(st.nextToken());
        }

        int num = 2;
        for(int i = 1; i <= K; i++){
            int x = 1;
            int y = monsters[i][0];
            int d = monsters[i][1];

            while(true){
                // 남쪽으로 이동
                if (map[x + 1][y - 1] == 0 && map[x + 2][y] == 0 && map[x + 1][y + 1] == 0) {
                    x++;
                    continue;
                }

                // 서쪽으로 이동
                else if (map[x - 1][y - 1] == 0 && map[x][y - 2] == 0 && map[x + 1][y - 2] == 0 && map[x + 1][y - 1] == 0 && map[x + 2][y - 1] == 0) {
                    x++;
                    y--;
                    d = (d + 3) % 4;
                    continue;
                }

                // 동쪽으로 이동
                else if (map[x - 1][y + 1] == 0 && map[x][y + 2] == 0 && map[x + 1][y + 1] == 0 && map[x + 1][y + 2] == 0 && map[x + 2][y + 1] == 0) {
                    x++;
                    y++;
                    d = (d + 1) % 4;
                    continue;
                } else {
                    break;
                }
            }

            if (x < 4) {
                initMap();
                num = 2;
            } else {
                map[x-1][y] = num;
                map[x][y] = num;
                map[x+1][y] = num;
                map[x][y-1] = num;
                map[x][y+1] = num;
                num++;
                exitMap[x + dx[d]][y + dy[d]] = 1;

                int[][] visited = new int[R + 4][C + 2];
                visited[x][y] = 1;
                int max = 0;
                Queue<int[]> queue = new LinkedList<>();
                queue.offer(new int[]{x, y});
                while (!queue.isEmpty()) {
                    int[] poll = queue.poll();
                    max = Math.max(max, poll[0]);
                    for (int dir = 0; dir < 4; dir++) {
                        int nextX = poll[0] + dx[dir];
                        int nextY = poll[1] + dy[dir];
                        if (visited[nextX][nextY] == 0 && (map[poll[0]][poll[1]] == map[nextX][nextY] || exitMap[poll[0]][poll[1]] == 1 && map[nextX][nextY] > 1)) {
                            queue.offer(new int[]{nextX, nextY});
                            visited[nextX][nextY] = 1;
                        }
                    }
                }
                answer += (max - 2);

            }
        }

        System.out.println(answer);
    }

    static void initMap() {
        map = new int[R + 4][C + 2];
        exitMap = new int[R + 4][C + 2];
        for (int i = 0; i < R + 3; i++) {
            map[i][0] = 1;
            map[i][C + 1] = 1;
        }
        for (int j = 0; j <= C + 1; j++) {
            map[R + 3][j] = 1;
        }
    }
}