import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
	
	static class Map {
		int wall;
		List<Integer> list;
		int size;
		
		public Map() {
			this.list = new ArrayList<>();
			this.size = 0;
		}
		
		public Map(int wall) {
			super();
			this.wall = wall;
			this.list = new ArrayList<>();
			this.size = 0;
		}
		
		public void add(int num) {
			this.list.add(num);
			this.size++;
		}
		
		public void delete(int num) {
			this.list.remove(Integer.valueOf(num));
			this.size--;
		}
	}
	
	static class Point {
		int x, y;

		public Point(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "Point [x=" + x + ", y=" + y + "]";
		}
	}
	
	static class People extends Point {
		int dist;

		public People(int x, int y) {
			super(x, y);
			this.dist = 0;
		}

		@Override
		public String toString() {
			return "People [dist=" + dist + "]";
		}
		
	}
	
	static int N, M, K;
	static Map[][] map;
	static People[] people;
	static int pCnt;
	static int[] dx = {-1, 1, 0, 0};
	static int[] dy = {0, 0, -1, 1};
	static Point exit;
	static boolean[] isFinished;
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
		map = new Map[N+1][N+1];
		people = new People[M+1];
		isFinished = new boolean[M+1];
		
		// map
		for(int i=1; i<=N; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j=1; j<=N; j++) {
				map[i][j] = new Map(Integer.parseInt(st.nextToken()));
			}
		}
		
		// 참가자
		for(int i = 1; i <= M; i++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			
			people[i] = new People(x, y);
			map[x][y].add(i);
		}
		
		// 출구 정보
		st = new StringTokenizer(br.readLine());
		exit = new Point(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
		
		// 시뮬레이션
		for(int turn=0; turn<K; turn++) {
			// 이동할 수 있는 참가자가 없다면
			if(pCnt == M) break;
			
			// 참가자 이동
			move();
			
			if(pCnt == M) break;
			// 미로 회전
			rotate();
		}
		
		// 결과 출력
		StringBuilder sb = new StringBuilder();
		int sum = 0;
		for(int i=1; i<=M; i++) {
			sum += people[i].dist;
		}
		sb.append(sum + "\n");
		
		sb.append(exit.x + " " + exit.y);
		System.out.println(sb.toString());

	}
	
	public static void move() {
		for(int idx = 1; idx <= M; idx++) {
			// 이미 출구인 참가자는 무시
			if(isFinished[idx]) continue;
			
			People cur = people[idx];
			int dir = getDir(cur.x, cur.y);
			
			// 움직일 수 없는 경우
			if(dir == -1) continue;
			
			// 이동
			// 기존에 있던 곳에서 없애기
			map[cur.x][cur.y].delete(idx);
			
			// 이동하기
			cur.x += dx[dir];
			cur.y += dy[dir];
			cur.dist++;
			
			// 만약 움직인 곳이 출구일 때 
			if(cur.x == exit.x && cur.y == exit.y) {
				isFinished[idx] = true;
				pCnt++;
			} else {
				map[cur.x][cur.y].add(idx);
			}
		}
	}
	
	public static void rotate() {
		// 가장 작은 정사각형
		int[] result = getSquare();
		int x = result[0];
		int y = result[1];
		int size = result[2];
		
		// 임시 배열 만들기
		Map[][] temp = new Map[N+1][N+1];
		
		// 회전해서 임시 배열에 저장하기
		for(int i=x; i<x+size; i++) {
			for(int j=y; j<y+size; j++) {
				int ox = i-x, oy = j-y;
				int rx = oy, ry = size - ox -1;
				temp[rx+x][ry+y] = new Map();
				temp[rx+x][ry+y].wall = map[i][j].wall > 0 ? map[i][j].wall-1 : 0;
				copyList(temp[rx+x][ry+y].list, map[i][j].list);
				temp[rx+x][ry+y].size = map[i][j].size;
			}
		}
		
		// 임시 배열을 다시 원본 배열으로 옮기기
		for(int i=x; i<x+size; i++) {
			for(int j=y; j<y+size; j++) {
				map[i][j].wall = temp[i][j].wall;
				copyList(map[i][j].list, temp[i][j].list);
				map[i][j].size = temp[i][j].size;
				// 회전하면서 사람들 좌표도 바꿈
				rotateCoor(map[i][j].list, i,j);
			}
		}
		
		// 출구 회전
		int ox = exit.x - x, oy = exit.y - y;
		int rx = oy, ry = size - ox -1;
		exit.x = rx+x;
		exit.y = ry+y;
 	}
	
	public static int getDir(int x, int y) {
		int dir = -1;
		int min = distance(x, y);
		
		for(int i=0; i<4; i++) {
			int nx = x + dx[i];
			int ny = y + dy[i];
			
			// 범위를 벗어나거나 벽이 있을 경우
			if(!isRange(nx, ny) || map[nx][ny].wall > 0) continue;
			
			int d = distance(nx, ny);
			if(min > d) {
				dir = i;
				min = d;
			}
		}
		return dir;
	}
	
	public static int[] getSquare() {
		int[] result = {0, 0, 0};
		for(int size=2; size<=N; size++) {
			for(int r = 1, rEnd = N-size+1; r<=rEnd; r++) {
				for(int c = 1, cEnd = N-size+1; c<=cEnd; c++) {
					boolean isExit = false, isPeople = false;
					for(int i=r; i<r+size; i++) {
						for(int j=c; j<c+size; j++) {
							if(i==exit.x && j==exit.y) {	// 출구인 경우
								isExit = true;
							} else if(map[i][j].size > 0) {
								isPeople = true;
							}
						}
					}
					
					if(isExit && isPeople) {
						result[0] = r;
						result[1] = c;
						result[2] = size;
						return result;
					}
				}
			}
		}
		
		return result;
	}
	
	public static List<Integer> copyList(List<Integer> dest, List<Integer> src) {
		dest.clear();
		for(int num : src) {
			dest.add(num);
		}
		return dest;
	}
	
	public static void rotateCoor(List<Integer> list, int x, int y) {
		for(int idx : list) {
			people[idx].x = x;
			people[idx].y = y;
		}
	}
	
	public static int distance(int x, int y) {
		return Math.abs(exit.x - x) + Math.abs(exit.y - y);
	}
	
	public static boolean isRange(int x, int y) {
		return x >= 1 && x <= N && y >= 1 && y <= N;
	}
}