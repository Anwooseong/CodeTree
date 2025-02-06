import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
	static int N, M, K;
	static int[][] arr;
	static int[][] playerMap;

	static class Point {
		int x;
		int y;

		Point(int a, int b) {
			x = a;
			y = b;
		}
	}

	static Point exit;
	static Point[] players;
	static int numPlayer;
	static int[] dist;
	static boolean[] isExit;
	static int[] dx = {-1, 1, 0, 0};
	static int[] dy = {0, 0, 1, -1};

	public static void main(String[] args) throws Exception {
		// input
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String[] inputs = in.readLine().split(" ");
		N = stoi(inputs[0]);
		M = stoi(inputs[1]);
		K = stoi(inputs[2]);

		arr = new int[N][N];
		playerMap = new int[N][N];
		for (int i = 0; i < N; ++i) {
			inputs = in.readLine().split(" ");
			for (int j = 0; j < N; ++j) {
				arr[i][j] = stoi(inputs[j]);
			}
		}
		players = new Point[M];
		for (int i = 0; i < M; ++i) {
			inputs = in.readLine().split(" ");
			int x = stoi(inputs[0]) - 1;
			int y = stoi(inputs[1]) - 1;
			playerMap[x][y] += 1 << i;
			players[i] = new Point(x, y);
		}
		inputs = in.readLine().split(" ");
		exit = new Point(stoi(inputs[0]) - 1, stoi(inputs[1]) - 1);
		numPlayer = M;
		dist = new int[M];
		isExit = new boolean[M];

		for (int t = 0; t < K; ++t) {
			if (numPlayer == 0)
				break;

			// 플레이어의 이동
			movePlayer();
			if (numPlayer == 0)
				break;
			
			// 미로의 회전
			rotateMaze();
		}
		int sum = 0;
		for (int i : dist)
			sum += i;
		StringBuilder sb = new StringBuilder();
		sb.append(sum).append("\n").append(exit.x+1).append(" ").append(exit.y+1);
		System.out.println(sb);
	}

	private static void movePlayer() {
		for (int i = 0; i < M; ++i) {
			// 이미 탈출한 플레이어
			if (isExit[i])
				continue;
			int base = getDistance(exit.x, exit.y, players[i].x, players[i].y);
			int min = Integer.MAX_VALUE;
			int dir = -1;
			for (int d = 0; d < 4; ++d) {
				int nx = players[i].x + dx[d];
				int ny = players[i].y + dy[d];
				// 범위 밖
				if (!isInRange(nx, ny))
					continue;

				int value = getDistance(exit.x, exit.y, nx, ny);

				// 최단거리로만 이동한다. 더 먼 방향으로는 이동X
				if (base < value)
					continue;

				if (arr[nx][ny] > 0)
					continue;

				if (value < min) {
					min = value;
					dir = d;
				}
			}
			// 모든 방향으로 갈 수 없는 상태.
			if (dir == -1)
				continue;

			playerMap[players[i].x][players[i].y] -= 1 << i;
			players[i].x = players[i].x + dx[dir];
			players[i].y = players[i].y + dy[dir];
			playerMap[players[i].x][players[i].y] += 1 << i;
			dist[i]++;

			if (players[i].x == exit.x && players[i].y == exit.y) {
				playerMap[players[i].x][players[i].y] -= 1 << i;
				numPlayer--;
				isExit[i] = true;
			}
		}
	}

	private static void rotateMaze() {
		int len = 0;
		int r = 0;
		int c = 0;
		boolean flag = false;
		for (int length = 2; length <= N; ++length) {
			for (int i = 0; i <= N - length; ++i) {
				for (int j = 0; j <= N - length; ++j) {
					flag = findSquare(i, j, length);
					if (flag) {
						r = i;
						c = j;
						len = length;
						flag = true;
						break;
					}
				}
				if (flag)
					break;
			}
			if (flag)
				break;
		}

		// 회전
		rotate(r, c, len);
	}

	private static void rotate(int r, int c, int len) {
		int[][] copy = new int[len][len];
		int[][] copyPlayer = new int[len][len];
		int nx = 0;
		int ny = 0;
		for (int i = 0; i < len; ++i) {
			for (int j = 0; j < len; ++j) {
				copy[j][len - 1 - i] = arr[i + r][j + c];
				copyPlayer[j][len - 1 - i] = playerMap[i + r][j + c];

				if (exit.x == i + r && exit.y == j + c) {
					nx = j + r;
					ny = len - 1 - i + c;
				}
			}
		}
		exit.x = nx;
		exit.y = ny;
		for (int i = r; i < r + len; ++i) {
			for (int j = c; j < c + len; ++j) {
				arr[i][j] = copy[i - r][j - c];
				if (arr[i][j] > 0)
					arr[i][j]--;

				playerMap[i][j] = copyPlayer[i - r][j - c];
				for (int k = 0; k < M; ++k) {
					int p = 1 << k;
					if ((playerMap[i][j] & p) == p) {
						players[k].x = i;
						players[k].y = j;
					}
				}
			}
		}
	}

	private static boolean findSquare(int i, int j, int length) {
		boolean hasPlayer = false;
		boolean hasExit = false;

		for (int x = i; x < i + length; ++x) {
			for (int y = j; y < j + length; ++y) {
				if (!hasPlayer) {
					if (playerMap[x][y] > 0)
						hasPlayer = true;
				}

				if (!hasExit)
					if (exit.x == x && exit.y == y)
						hasExit = true;

				if (hasPlayer && hasExit)
					return true;
			}
		}
		return false;
	}

	public static boolean isInRange(int x, int y) {
		if (0 <= x && x < N && 0 <= y && y < N)
			return true;
		return false;
	}

	public static int getDistance(int x1, int y1, int x2, int y2) {
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
	}

	public static int stoi(String s) {
		return Integer.parseInt(s);
	}
}