import java.util.*;
import java.io.*;

class Square {
    int startX;
    int startY;
    int L;

    public Square(int startX, int startY, int l) {
        this.startX = startX;
        this.startY = startY;
        L = l;
    }
}

public class Main {

    static int[][] map;
    static final int PERSON = -1, EXIT = -100;
    static int N, M, K, answer, cnt;
    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, -1, 1};

    public static void main(String[] args) throws Exception {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(bf.readLine(), " ");

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        // 맵 설정
        map = new int[N][N];
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(bf.readLine(), " ");
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 참가자 설정
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(bf.readLine(), " ");
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            map[x][y] += PERSON;
        }

        //탈출구 설정
        st = new StringTokenizer(bf.readLine(), " ");
        int exitX = Integer.parseInt(st.nextToken()) - 1;
        int exitY = Integer.parseInt(st.nextToken()) - 1;
        map[exitX][exitY] = EXIT;

        answer = 0;
        cnt = M;
        for (int time = 0; time < K; time++) {
            if (cnt == 0) break;
            int[][] newMap = new int[N][N];
            copyMap(newMap);
            movePerson(exitX, exitY, newMap);
            map = newMap;

            if (cnt == 0) break;

            //정사각형 찾기
            Square square = findSquare(exitX, exitY);

            //회전
            int[][] rotateMap = new int[N][N];
            copyMap(rotateMap);
            for (int i = 0; i < square.L; i++) {
                for (int j = 0; j < square.L; j++) {
                    rotateMap[square.startX + i][square.startY + j] = map[square.startX + square.L - 1 - j][square.startY + i];
                    if (rotateMap[square.startX + i][square.startY + j] > 0) {
                        rotateMap[square.startX + i][square.startY + j] -= 1;
                    }
                }
            }
            map = rotateMap;

            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (map[i][j] == EXIT) {
                        exitX = i;
                        exitY = j;
                    }
                }
            }

        }

        System.out.println(answer);
        System.out.println((exitX + 1) + " " + (exitY + 1));
    }

    static Square findSquare(int exitX, int exitY) {
        int length = N;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (map[i][j] < 0 && map[i][j] > EXIT) {
                    length = Math.min(length, Math.max(Math.abs(exitX - i), Math.abs(exitY - j)));
                }
            }
        }

        for (int i = 0; i < N - length; i++) {
            for (int j = 0; j < N - length; j++) {
                if (i <= exitX && exitX <= i + length && j <= exitY && exitY <= j + length) {
                    for (int k = i; k < i + length + 1; k++) {
                        for (int l = j; l < j + length + 1; l++) {
                            if (map[k][l] < 0 && map[k][l] > EXIT) {
                                return new Square(i, j, length + 1);
                            }
                        }
                    }
                }
            }
        }
        return new Square(0, 0, 0);
    }

    static void movePerson(int exitX, int exitY, int[][] newMap) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (map[i][j] < 0 && map[i][j] > EXIT) { //사람인 경우 최단거리 파악
                    int originDistance = Math.abs(i - exitX) + Math.abs(j - exitY);
                    for (int k = 0; k < 4; k++) {
                        int nextX = i + dx[k];
                        int nextY = j + dy[k];
                        int newDistance = Math.abs(nextX - exitX) + Math.abs(nextY - exitY);
                        if (isInRange(nextX, nextY) && newMap[nextX][nextY] <= 0) {
                            if (originDistance > newDistance) {
                                if (newMap[nextX][nextY] == EXIT) {
                                    cnt += newMap[i][j];
                                } else {
                                    newMap[nextX][nextY] += newMap[i][j];
                                }
                                answer += (-newMap[i][j]);
                                newMap[i][j] = 0;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    static boolean isInRange(int nextX, int nextY) {
        return nextX >= 0 && nextX < N && nextY >= 0 && nextY < N;
    }

    static void copyMap(int[][] newMap) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                newMap[i][j] = map[i][j];
            }
        }
    }
}