import java.util.*;
import java.io.*;

class Person {
    int x;
    int y;
    int d;
    int s;
    int gunPower;

    public Person(int x, int y, int d, int s, int gunPower) {
        this.x = x;
        this.y = y;
        this.d = d;
        this.s = s;
        this.gunPower = gunPower;
    }
}

public class Main {

    static int n, m, k;
    static PriorityQueue<Integer>[][] map;
    static int[][] personMap;
    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};
    static Person[] persons;
    static int[] answer;

    public static void main(String[] args) throws Exception {
        init();
        for (int time = 1; time <= k; time++) {
            simulation();
        }
        for (int i = 1; i <= m; i++) {
            System.out.print(answer[i]+" ");
        }
    }

    static void simulation() {
        step_1(); // 첫번째 플레이어부터 순차적으로 이동
    }

    static void step_1() {
        for (int i = 1; i <= m; i++) {
            Person selectedPerson = persons[i];
            int x = selectedPerson.x;
            int y = selectedPerson.y;
            int d = selectedPerson.d;

            int nextX = x + dx[d];
            int nextY = y + dy[d];
            if (isInRage(nextX, nextY)) {
                persons[i].x = nextX;
                persons[i].y = nextY;
                personMap[x][y] = 0;
                if (personMap[nextX][nextY] != 0) {//이동한 방향에 플레이어가 있다면
                    step2_2_1(i, personMap[nextX][nextY]);
                } else {
                    step2_1(i);
                }
            }
            else if (!isInRage(nextX, nextY)) {
                //정반대 방향으로 방향을 바꿔서 1만큼 이동
                d = (d + 2) % 4;
                nextX = x + dx[d];
                nextY = y + dy[d];
                persons[i].x = nextX;
                persons[i].y = nextY;
                persons[i].d = d;
                personMap[x][y] = 0;
                if (personMap[nextX][nextY] != 0) {//이동한 방향에 플레이어가 있다면
                    step2_2_1(i, personMap[nextX][nextY]);
                } else {
                    step2_1(i);
                }
            }
        }
    }

    static void step2_2_1(int originNum, int targetNum) {
        int originS = persons[originNum].s;
        int originGunPower = persons[originNum].gunPower;
        int targetS = persons[targetNum].s;
        int targetGunPower = persons[targetNum].gunPower;

        int loserNum;
        int winnerNum;
        if (originS + originGunPower == targetS + targetGunPower) {
            loserNum = originS < targetS ? originNum : targetNum;
            winnerNum = originS < targetS ? targetNum : originNum;
        } else {
            loserNum = originS + originGunPower < targetS + targetGunPower ? originNum : targetNum;
            winnerNum = originS + originGunPower < targetS + targetGunPower ? targetNum : originNum;
        }
        answer[winnerNum] += Math.abs((persons[winnerNum].s + persons[winnerNum].gunPower) - (persons[loserNum].s + persons[loserNum].gunPower));
        personMap[persons[winnerNum].x][persons[winnerNum].y] = winnerNum;

        step2_2_2(loserNum);

        //이긴 사람은 들고있던 총과 격자에 떨어져 있는 총중에 가장 높은 총 획득후 나머지 총 버림
        step2_2_3(winnerNum);
    }

    static void step2_2_3(int winnerNum) {
        Person person = persons[winnerNum];
        int x = person.x;
        int y = person.y;
        int d = person.d;
        int gunPower = person.gunPower;

        if (!map[x][y].isEmpty()) {
            int peek = map[x][y].peek();
            if (gunPower < peek) { //지도에 놓여진 총이 가지고 있는 총보다 쎌 때 자기총 버리고 줍기
                int poll = map[x][y].poll();
                int tmp = gunPower;
                persons[winnerNum].gunPower = poll;
                map[x][y].offer(tmp);
            }
        }
    }

    static void step2_2_2(int loserNum) {
        //진 사람은 총을 놓고 원래 가지고 있던 방향대로 이동, 사람이 있거라 격자 밖이면 90도씩 오른쪽회전
        //빈칸이 보이는 순간 이동, 만약 그 고셍 총이 있다면, 가장 공격력이 높은 총 획득
        Person person = persons[loserNum];
        int x = person.x;
        int y = person.y;
        int d = person.d;
        int gunPower = person.gunPower;

        if (gunPower != 0) map[x][y].offer(gunPower);
        persons[loserNum].gunPower = 0;
        int nextX = x + dx[d];
        int nextY = y + dy[d];
        while (true) {
            if (!isInRage(nextX, nextY)) {
                //정반대 방향으로 방향을 바꿔서 1만큼 이동
                d = (d + 1) % 4;
                persons[loserNum].d = d;
                nextX = x + dx[d];
                nextY = y + dy[d];
            } else {
                if (personMap[nextX][nextY] != 0) {
                    d = (d + 1) % 4;
                    persons[loserNum].d = d;
                    nextX = x + dx[d];
                    nextY = y + dy[d];
                } else if (personMap[nextX][nextY] == 0) {
                    break;
                }
            }
        }

        persons[loserNum].x = nextX;
        persons[loserNum].y = nextY;
        personMap[nextX][nextY] = loserNum;
        if (!map[nextX][nextY].isEmpty()) {
            persons[loserNum].gunPower = map[nextX][nextY].poll();
        }

    }

    static void step2_1(int num) {
        Person person = persons[num];
        int x = person.x;
        int y = person.y;
        int d = person.d;
        int s = person.s;
        int gunPower = person.gunPower;

        personMap[x][y] = num;
        if (!map[x][y].isEmpty()) {
            int peek = map[x][y].peek();
            if (gunPower < peek) { //지도에 놓여진 총이 가지고 있는 총보다 쎌 때 자기총 버리고 줍기
                int poll = map[x][y].poll();
                int tmp = gunPower;
                persons[num].gunPower = poll;
                map[x][y].offer(tmp);
            }
        }
    }

    static boolean isInRage(int x, int y) {
        return x >= 0 && x < n && y >= 0 && y < n;
    }

    static void init() throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(bf.readLine(), " ");
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        answer = new int[m + 1];

        map = new PriorityQueue[n][n];
        personMap = new int[n][n];

        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(bf.readLine(), " ");
            for (int j = 0; j < n; j++) {
                map[i][j] = new PriorityQueue<>(Collections.reverseOrder());
                map[i][j].offer(Integer.parseInt(st.nextToken()));
            }
        }

        persons = new Person[m + 1];
        for (int i = 1; i <= m; i++) {
            st = new StringTokenizer(bf.readLine(), " ");
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            int d = Integer.parseInt(st.nextToken());
            int s = Integer.parseInt(st.nextToken());
            int gunPower = 0;
            personMap[x][y] = i;
            if (!map[x][y].isEmpty()) {
                gunPower = map[x][y].poll();
            }
            persons[i] = new Person(x, y, d, s, gunPower);
        }
    }
}
