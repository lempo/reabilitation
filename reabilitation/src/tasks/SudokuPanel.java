package tasks;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.swing.JPanel;

public class SudokuPanel extends JPanel implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = -2648074700623502235L;
	private int width;
	private int height;
	private int[][] grid;
	private int[][] user;
	private int[][] menu;
	private int[][] solution;
	private int editX;
	private int editY;

	private int gridSize;

	private int diff;

	private Color gridColor = new Color(68, 83, 91);
	private Color highlightColor = new Color(214, 204, 199);
	private Color gridHighlightColor = new Color(238, 82, 80);
	private Color userNumColor = new Color(38, 166, 154);
	private Color menuNumColor = new Color(102, 102, 102);
	private BasicStroke gridPen = new BasicStroke(2);

	private int selectedX = 0;
	private int selectedY = 0;

	private boolean pause = false;
	private boolean canPause = true;

	private long pauseTime = 0;
	private long startPauseTime = 0;

	private Sudoku w;

	public SudokuPanel(int width, int height, int diff, Sudoku w) {
		super();
		this.width = width;
		this.height = height;
		this.diff = diff;
		this.w = w;
		this.setDoubleBuffered(true);
		this.setOpaque(false);
		this.setPreferredSize(new Dimension(width, height));
		grid = new int[9][9];
		user = new int[9][9];
		menu = new int[9][9];
		init();
	}
	
	public void fromBegining() {
		pauseTime = 0;
		startPauseTime = 0;
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++) {
				if (user[i][j] == 1)
					grid[i][j] = 0;
				menu[i][j] = 0;
			}
		selectedX = 0;
		selectedY = 0;
		editX = 0;
		editY = 0;
	}

	private void init() {
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++) {
				grid[i][j] = (i * 3 + i / 3 + j) % 9 + 1;
				user[i][j] = 0;
				menu[i][j] = 0;
			}
		
		randomShuffle(100);
		
		build(diff);
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				if (grid[i][j] == 0)
					user[i][j] = 1;

		addMouseListener(this);
		addMouseMotionListener(this);

		solution = solve(grid);
		print(solution);
	}

	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		if ((selectedX != 0) && (selectedY != 0) && (user[selectedY - 1][selectedX - 1] != 0)) {
			fillColorBar(g, selectedX - 1, selectedY - 1, highlightColor);
			editY = selectedY - 1;
			editX = selectedX - 1;
		}
		drawGrid(g);
		drawNumbers(g);
		if ((selectedX != 0) && (selectedY != 0) && (user[selectedY - 1][selectedX - 1] != 0)) {
			drawMenuColor(g);
			if (grid[selectedY - 1][selectedX - 1] != 0)
				drawCross(g);
			drawMenuContur(g);
			drawMenuNumbers(g);
		}
		g.dispose();
	}

	private void drawGrid(Graphics2D g) {
		g.setColor(gridColor);
		g.setStroke(gridPen);
		gridSize = Math.min(width, height) / 9;
		for (int i = 0; i <= 9; i++) {
			g.drawLine(i * gridSize, 0, i * gridSize, 9 * gridSize);
		}
		for (int i = 0; i <= 9; i++) {
			g.drawLine(0, i * gridSize, 9 * gridSize, i * gridSize);
		}

		g.setStroke(new BasicStroke(gridPen.getLineWidth() + 2));

		g.drawLine((int) (gridPen.getLineWidth() / 2 + 1), 0, (int) (gridPen.getLineWidth() / 2 + 1), 9 * gridSize);
		for (int i = 0; i <= 3; i++) {
			g.drawLine(i * gridSize * 3, 0, i * gridSize * 3, 9 * gridSize);
		}
		g.drawLine(0, (int) (gridPen.getLineWidth() / 2 + 1), 9 * gridSize, (int) (gridPen.getLineWidth() / 2 + 1));
		for (int i = 0; i <= 3; i++) {
			g.drawLine(0, i * gridSize * 3, 9 * gridSize, i * gridSize * 3);
		}
	}

	private void drawNumbers(Graphics2D g) {
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				if (grid[i][j] != 0)
					if (user[i][j] == 0)
						drawNum(g, j, i, gridColor, grid[i][j]);
					else
						drawNum(g, j, i, userNumColor, grid[i][j]);
	}

	private void drawCross(Graphics2D g) {
		g.setStroke(new BasicStroke(gridPen.getLineWidth() + 3));
		if (selectedX > 5) {
			if (selectedY > 5) {
				fillColorBar(g, selectedX - 4, selectedY - 4, highlightColor);
				g.setColor(gridColor);
				g.drawLine((int) ((selectedX - 4) * gridSize + gridSize * 0.3),
						(int) ((selectedY - 4) * gridSize + gridSize * 0.3),
						(int) ((selectedX - 3) * gridSize - gridSize * 0.3),
						(int) ((selectedY - 3) * gridSize - gridSize * 0.3));
				g.drawLine((int) ((selectedX - 4) * gridSize + gridSize * 0.3),
						(int) ((selectedY - 3) * gridSize - gridSize * 0.3),
						(int) ((selectedX - 3) * gridSize - gridSize * 0.3),
						(int) ((selectedY - 4) * gridSize + gridSize * 0.3));
			} else {
				fillColorBar(g, selectedX - 4, selectedY, highlightColor);
				g.setColor(gridColor);
				g.drawLine((int) ((selectedX - 4) * gridSize + gridSize * 0.3),
						(int) (selectedY * gridSize + gridSize * 0.3),
						(int) ((selectedX - 3) * gridSize - gridSize * 0.3),
						(int) ((selectedY + 1) * gridSize - gridSize * 0.3));
				g.drawLine((int) ((selectedX - 4) * gridSize + gridSize * 0.3),
						(int) ((selectedY + 1) * gridSize - gridSize * 0.3),
						(int) ((selectedX - 3) * gridSize - gridSize * 0.3),
						(int) (selectedY * gridSize + gridSize * 0.3));
			}
		} else {
			if (selectedY > 5) {
				fillColorBar(g, selectedX, selectedY - 4, highlightColor);
				g.setColor(gridColor);
				g.drawLine((int) (selectedX * gridSize + gridSize * 0.3),
						(int) ((selectedY - 4) * gridSize + gridSize * 0.3),
						(int) ((selectedX + 1) * gridSize - gridSize * 0.3),
						(int) ((selectedY - 3) * gridSize - gridSize * 0.3));
				g.drawLine((int) (selectedX * gridSize + gridSize * 0.3),
						(int) ((selectedY - 3) * gridSize - gridSize * 0.3),
						(int) ((selectedX + 1) * gridSize - gridSize * 0.3),
						(int) ((selectedY - 4) * gridSize + gridSize * 0.3));
			} else {
				fillColorBar(g, selectedX, selectedY, highlightColor);
				g.setColor(gridColor);
				g.drawLine((int) (selectedX * gridSize + gridSize * 0.3), (int) (selectedY * gridSize + gridSize * 0.3),
						(int) ((selectedX + 1) * gridSize - gridSize * 0.3),
						(int) ((selectedY + 1) * gridSize - gridSize * 0.3));
				g.drawLine((int) (selectedX * gridSize + gridSize * 0.3),
						(int) ((selectedY + 1) * gridSize - gridSize * 0.3),
						(int) ((selectedX + 1) * gridSize - gridSize * 0.3),
						(int) (selectedY * gridSize + gridSize * 0.3));
			}
		}
	}

	private void drawMenuNumbers(Graphics2D g) {
		int i = 1;
		if (selectedX > 5) {
			if (selectedY > 5) {
				for (int k = 0; k < 3; k++)
					for (int j = 0; j < 3; j++) {
						if ((k == 0) && (j == 0) && (grid[selectedY - 1][selectedX - 1] != 0)) {
							menu[selectedY - 4 + k][selectedX - 4 + j] = -1;
							continue;
						}
						if (i == grid[selectedY - 1][selectedX - 1])
							i++;
						drawNum(g, selectedX - 4 + j, selectedY - 4 + k, menuNumColor, i);
						menu[selectedY - 4 + k][selectedX - 4 + j] = i;
						i++;
					}
			} else {
				for (int k = 0; k < 3; k++)
					for (int j = 0; j < 3; j++) {
						if ((k == 0) && (j == 0) && (grid[selectedY - 1][selectedX - 1] != 0)) {
							menu[selectedY + k][selectedX - 4 + j] = -1;
							continue;
						}
						if (i == grid[selectedY - 1][selectedX - 1])
							i++;
						drawNum(g, selectedX - 4 + j, selectedY + k, menuNumColor, i);
						menu[selectedY + k][selectedX - 4 + j] = i;
						i++;
					}
			}
		} else {
			if (selectedY > 5) {
				for (int k = 0; k < 3; k++)
					for (int j = 0; j < 3; j++) {
						if ((k == 0) && (j == 0) && (grid[selectedY - 1][selectedX - 1] != 0)) {
							menu[selectedY - 4 + k][selectedX + j] = -1;
							continue;
						}
						if (i == grid[selectedY - 1][selectedX - 1])
							i++;
						drawNum(g, selectedX + j, selectedY - 4 + k, menuNumColor, i);
						menu[selectedY - 4 + k][selectedX + j] = i;
						i++;
					}
			} else {
				for (int k = 0; k < 3; k++)
					for (int j = 0; j < 3; j++) {
						if ((k == 0) && (j == 0) && (grid[selectedY - 1][selectedX - 1] != 0)) {
							menu[selectedY + k][selectedX + j] = -1;
							continue;
						}
						if (i == grid[selectedY - 1][selectedX - 1])
							i++;
						drawNum(g, selectedX + j, selectedY + k, menuNumColor, i);
						menu[selectedY + k][selectedX + j] = i;
						i++;
					}
			}
		}
	}

	private void drawMenuColor(Graphics2D g) {
		if (selectedX > 5) {
			if (selectedY > 5) {
				for (int k = 0; k < 3; k++)
					for (int j = 0; j < 3; j++)
						fillColorBar(g, selectedX - 4 + j, selectedY - 4 + k, Color.WHITE);
			} else {
				for (int k = 0; k < 3; k++)
					for (int j = 0; j < 3; j++)
						fillColorBar(g, selectedX - 4 + j, selectedY + k, Color.WHITE);
			}
		} else {
			if (selectedY > 5) {
				for (int k = 0; k < 3; k++)
					for (int j = 0; j < 3; j++)
						fillColorBar(g, selectedX + j, selectedY - 4 + k, Color.WHITE);
			} else {
				for (int k = 0; k < 3; k++)
					for (int j = 0; j < 3; j++)
						fillColorBar(g, selectedX + j, selectedY + k, Color.WHITE);
			}
		}
	}

	private void drawMenuContur(Graphics2D g) {
		if (selectedX > 5) {
			if (selectedY > 5) {
				for (int k = 0; k < 3; k++)
					for (int j = 0; j < 3; j++)
						drawColorBar(g, selectedX - 4 + j, selectedY - 4 + k, gridHighlightColor);
			} else {
				for (int k = 0; k < 3; k++)
					for (int j = 0; j < 3; j++)
						drawColorBar(g, selectedX - 4 + j, selectedY + k, gridHighlightColor);
			}
		} else {
			if (selectedY > 5) {
				for (int k = 0; k < 3; k++)
					for (int j = 0; j < 3; j++)
						drawColorBar(g, selectedX + j, selectedY - 4 + k, gridHighlightColor);
			} else {
				for (int k = 0; k < 3; k++)
					for (int j = 0; j < 3; j++)
						drawColorBar(g, selectedX + j, selectedY + k, gridHighlightColor);
			}
		}
	}

	private void fillColorBar(Graphics2D g, int x, int y, Color color) {
		g.setColor(color);
		g.fillRect(x * gridSize, y * gridSize, gridSize, gridSize);
	}

	private void drawNum(Graphics2D g, int x, int y, Color color, int i) {
		g.setColor(color);
		Font f = new Font("Arial Narrow", Font.PLAIN, (int) (gridSize * 0.9));
		g.setFont(f);
		FontMetrics fm = g.getFontMetrics();
		int shiftH = (fm.getAscent() - fm.getDescent()) / 2;

		g.drawString(Integer.toString(i),
				(int) (gridSize * (x + 0.5)) - fm.charWidth(Integer.toString(i).charAt(0)) / 2,
				(int) (gridSize * (y + 0.5)) + shiftH);

	}

	private void drawColorBar(Graphics2D g, int x, int y, Color color) {
		g.setStroke(new BasicStroke(gridPen.getLineWidth() + 2));
		g.setColor(color);
		g.drawRect(x * gridSize, y * gridSize, gridSize, gridSize);
	}

	private void transpose() {
		int[][] g = new int[9][9];
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				g[i][j] = grid[j][i];
		grid = g;
	}

	private void swapRows() {
		Random rand = new Random(new Date().getTime());
		int area = rand.nextInt(3);
		int row1 = rand.nextInt(3);
		int row2 = rand.nextInt(3);

		row1 = area * 3 + row1;
		row2 = area * 3 + row2;

		int swap;
		for (int i = 0; i < 9; i++) {
			swap = grid[row1][i];
			grid[row1][i] = grid[row2][i];
			grid[row2][i] = swap;
		}
	}

	private void swapCols() {
		Random rand = new Random(new Date().getTime());
		int area = rand.nextInt(3);
		int col1 = rand.nextInt(3);
		int col2 = rand.nextInt(3);

		col1 = area * 3 + col1;
		col2 = area * 3 + col2;

		int swap;
		for (int i = 0; i < 9; i++) {
			swap = grid[i][col1];
			grid[i][col1] = grid[i][col2];
			grid[i][col2] = swap;
		}
	}

	private void swapAreaRows() {
		Random rand = new Random(new Date().getTime());
		int area1 = rand.nextInt(3);
		int area2 = rand.nextInt(3);

		int swap;
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 3; j++) {
				swap = grid[area1 * 3 + j][i];
				grid[area1 * 3 + j][i] = grid[area2 * 3 + j][i];
				grid[area2 * 3 + j][i] = swap;
			}
	}

	private void swapAreaCols() {
		Random rand = new Random(new Date().getTime());
		int area1 = rand.nextInt(3);
		int area2 = rand.nextInt(3);

		int swap;
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 3; j++) {
				swap = grid[i][area1 * 3 + j];
				grid[i][area1 * 3 + j] = grid[i][area2 * 3 + j];
				grid[i][area2 * 3 + j] = swap;
			}
	}

	private boolean canPlace(int i, int j, int k, int[][] g) {
		for (int p = 0; p < 9; p++)
			if (g[i][p] == k)
				return false;
		for (int p = 0; p < 9; p++)
			if (g[p][j] == k)
				return false;
		int mini = i - i % 3;
		int maxi = i + (3 - i % 3);
		int minj = j - j % 3;
		int maxj = j + (3 - j % 3);
		for (int p = mini; p < maxi; p++)
			for (int h = minj; h < maxj; h++)
				if (g[p][h] == k)
					return false;
		return true;
	}

	private int[] isNextUnknown(int[][] g) {
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				if (g[i][j] == 0)
					return new int[] { i, j };
		return null;
	}

	private ArrayList<Integer> possible(int i, int j, int[][] g) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int p = 1; p <= 9; p++)
			if (canPlace(i, j, p, g))
				a.add(p);
		return a;
	}

	private int[][] solve(int[][] g) {
		int[][] solution = new int[9][9];
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				solution[i][j] = g[i][j];
		if (solveHelper1(solution) == 1) {
			return solution;
		}
		return null;
	}

	private int solveHelper1(int[][] solution) {
		int minRow = -1;
		int minColumn = -1;
		ArrayList<Integer> minValues = null;
		while (true) {
			minRow = -1;
			for (int rowIndex = 0; rowIndex < 9; rowIndex++) {
				for (int columnIndex = 0; columnIndex < 9; columnIndex++) {
					if (solution[rowIndex][columnIndex] != 0) {
						continue;
					}
					ArrayList<Integer> possibleValues = possible(rowIndex, columnIndex, solution);
					int possibleVaueCount = possibleValues.size();
					if (possibleVaueCount == 0) {
						return 0;
					}
					if (possibleVaueCount == 1) {
						solution[rowIndex][columnIndex] = possibleValues.get(0);
					}
					if (minRow < 0 || possibleVaueCount < minValues.size()) {
						minRow = rowIndex;
						minColumn = columnIndex;
						minValues = possibleValues;
					}
				}
			}
			if (minRow == -1) {
				return 1;
			} else if (1 < minValues.size()) {
				break;
			}
		}
		int f = 0;
		for (int v = 0; v < minValues.size(); v++) {
			int[][] solutionCopy = solution;
			solutionCopy[minRow][minColumn] = minValues.get(v);
			if (solveHelper1(solutionCopy) >= 1) {
				solution = solutionCopy;
				f++;
			}
		}
		return f;
	}

	private void randomShuffle(int num) {
		Class c = this.getClass();
		Method[] methods = null;
		try {
			Method m1 = c.getDeclaredMethod("transpose", null);
			Method m2 = c.getDeclaredMethod("swapRows", null);
			Method m3 = c.getDeclaredMethod("swapCols", null);
			Method m4 = c.getDeclaredMethod("swapAreaRows", null);
			Method m5 = c.getDeclaredMethod("swapAreaCols", null);
			methods = new Method[] { m1, m2, m3, m4, m5 };
		} catch (NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
		}
		Random rand = new Random(new Date().getTime());
		for (int i = 0; i < num; i++) {
			int j = rand.nextInt(methods.length);
			try {
				methods[j].invoke(this, null);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private void build(int diff) {
		int d = 0;
		Random rand = new Random(new Date().getTime());
		int[][] check = new int[9][9];
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				check[i][j] = 0;
		while (d < diff) {
			int row = rand.nextInt(9);
			int col = rand.nextInt(9);
			if ((grid[row][col] != 0) && (check[row][col] != 1)) {
				int tmp = grid[row][col];
				grid[row][col] = 0;
				if (solve(grid) != null)
					d++;
				else
					grid[row][col] = tmp;
				check[row][col] = 1;
			}
		}
	}

	public int getCorrectPercent() {
		int correct = 0;
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				if (user[i][j] != 0)
					if (grid[i][j] == solution[i][j])
						correct++;
		return (int) Math.round(((float) correct / diff) * 100.0);
	}

	private void print(int[][] g) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++)
				System.out.print(g[i][j] + " ");
			System.out.println();
		}
	}

	public long getPauseTime() {
		return pauseTime;
	}

	public boolean pause() {
		if (canPause) {
			pause = true;
			startPauseTime = new Date().getTime();
			return true;
		} else
			return false;
	}

	public void start() {
		if (pause) {
			pauseTime += new Date().getTime() - startPauseTime;
			pause = false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (pause)
			return;
		Point p1 = e.getLocationOnScreen();
		Point p2 = this.getLocationOnScreen();
		selectedX = (int) Math.ceil((p1.x - p2.x) / (float) gridSize);
		selectedY = (int) Math.ceil((p1.y - p2.y) / (float) gridSize);
		if ((selectedX - 1 > 8) || (selectedY - 1 > 8) || (selectedX - 1 < 0) || (selectedY - 1 < 0)) {
			selectedX = 0;
			selectedY = 0;
			return;
		}
		if (menu[selectedY - 1][selectedX - 1] != 0) {
			if (menu[selectedY - 1][selectedX - 1] == -1)
				grid[editY][editX] = 0;
			else {
				grid[editY][editX] = menu[selectedY - 1][selectedX - 1];
				int f = 0;
				for (int i = 0; i < 9; i++)
					for (int j = 0; j < 9; j++)
						if (grid[i][j] == 0)
							f = 1;
				if (f == 0)
					w.showResults();
			}
			selectedX = 0;
			selectedY = 0;
		}
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				menu[i][j] = 0;
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point p1 = e.getLocationOnScreen();
		Point p2 = this.getLocationOnScreen();
		int x = (int) Math.ceil((p1.x - p2.x) / (float) gridSize) - 1;
		int y = (int) Math.ceil((p1.y - p2.y) / (float) gridSize) - 1;
		if ((x > 8) || (y > 8) || (x < 0) || (y < 0))
			return;
		if ((menu[y][x] != 0) || (user[y][x] != 0))
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		else
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}
