package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Controller {
    private final int MAX_POINTS = 20;
    private final int MAX_LINES = 400;
    private final int SIZE = 150;
    private final int RADIUS = 20;
    private final int RADIUS2 = RADIUS * 2;
    private final int RADIUS3 = RADIUS + 3;

    @FXML
    public Label lblSteps;
    public Label lblNodes;
    public Label lblPointEnd;
    public Label lblPointStart;
    public Button btnGenerate;
    public Button btnFind;
    public TextField txtPoint;
    public TextField txtLine;
    public CheckBox cbShowStep;
    public Slider sliderSpeed;
    //    public ImageView imageView;
    public ScrollPane paneDraw;
    public Label lblStatus;

    // Node in graph
    ArrayList<Circle> nodes = new ArrayList<>();
    Line[][] line = new Line[MAX_LINES][MAX_LINES];

    // pane graph
    Pane root;
    Pane rootOld;
    Point p;

    private Random rd = new Random();
    private Font drawFont = new Font("Times New Roman Bold", 18);
    private int countPoint, countLine;
    private Point[] points = new Point[MAX_POINTS];
    private PairPoint[][] lines = new PairPoint[MAX_POINTS][MAX_POINTS];
    private int[][] distance = new int[MAX_POINTS][MAX_POINTS];
    private int[] newDistance = new int[MAX_POINTS];
    private int start = -1;
    private int dest = -1;
    private int countSteps = 0;
    private int maxSteps = 0;
    private Point[] listSteps = new Point[MAX_LINES];
    private List<Integer> listPath = new ArrayList<Integer>();
    private boolean isDone = false;
    private boolean isFounded = false;
    private int chooseState = 0;
    private String[] textChoose = {"vuongdq85", "Choose node start...", "Choose node end..."};

    /* initialize */

    public void initialize() {
        countPoint = Integer.parseInt(txtPoint.getText());
        countLine = Integer.parseInt(txtLine.getText());

        reset();
        generate();
    }

    /* Event handle */

    public void cbShowStep_Clicked(MouseEvent mouseEvent) {
        if (cbShowStep.isSelected()) System.out.println("cbShowStep");
    }

    public void btnGenerate_Clicked(MouseEvent mouseEvent) {
        reset();
        boolean ok = false;

        try {
            countPoint = Integer.parseInt(txtPoint.getText());
            countLine = Integer.parseInt(txtLine.getText());

            System.out.println(countPoint);
            System.out.println(countLine);

            if (countPoint > 0
                    && countPoint <= 20
                    && countLine > 0
                    && countLine <= countPoint * (countPoint - 1))
                ok = true;
        } catch (Exception e) { e.printStackTrace(); }

        if (ok) {
            generate();
        } else {
            AlertWarning("Alert", null, "Wrong amount of points");
        }
    }

    public void paneDraw_clicked(MouseEvent mouseEvent) {
        MouseEvent me = mouseEvent;
        if (chooseState == 1) {
            for (int i = 0; i < countPoint; i++) {
                if (me.getX() > points[i].x - RADIUS
                        && me.getX() < points[i].x + RADIUS
                        && me.getY() > points[i].y - RADIUS
                        && me.getY() < points[i].getY() + RADIUS) {
                    start = i;
                    lblPointStart.setText("Point start: " + i);

                    Circle cStart = nodes.get(start);
                    System.out.println("Start: " + i + ". (" + cStart.getCenterX() + "," + cStart.getCenterY()+")");

                    // draw point start
                    var temp = drawCircle(points[i], i, Color.GREEN);
                    root.getChildren().addAll(temp.getKey(), temp.getValue());

                    chooseState = 2;
                    lblStatus.setText(textChoose[chooseState]);
                    break;
                }
            }
        } else if (chooseState == 2) {
            for (int i = 0; i < countPoint; i++) {
                if (me.getX() > points[i].x - RADIUS
                        && me.getX() < points[i].x + RADIUS
                        && me.getY() > points[i].y - RADIUS
                        && me.getY() < points[i].y + RADIUS) {
                    dest = i;
                    lblPointEnd.setText("Point end: " + i);
                    Circle cEnd = nodes.get(dest);
                    System.out.println("End: " + i + ". (" + cEnd.getCenterX() + "," + cEnd.getCenterY()+")");

                    // draw point end
                    var temp = drawCircle(points[i], i, Color.RED);
                    root.getChildren().addAll(temp.getKey(), temp.getValue());

                    chooseState = 0;
                    lblStatus.setText(textChoose[chooseState]);

                    // draw path
                    if (Dijkstra()) {
                        isDone = true;

                        if (isFounded) {
                            if (cbShowStep.isSelected()) drawPathStep();
                            else drawPath();
                        }
                    } else {
                        AlertWarning("Warning", null, "Not found!");
                    }
                    break;
                }
            }
        }
        paneDraw.setContent(root);
    }

    public void btnFind_Clicked(MouseEvent mouseEvent) {
        start = dest = -1;
        isFounded = false;

        // repaint graph
        repaintGraph();

        chooseState = 1;
        lblStatus.setText(textChoose[chooseState]);

    }

    public void sliderSpeed_Scroll(ScrollEvent scrollEvent) {
        //
        System.out.println(sliderSpeed.getValue());
    }

    /* Function */

    // draw path steps
    public void drawPathStep() {
        int old, n;
        Arrow arrow;

        // draw step
        for (int i = 0; i < maxSteps; i++) {
            //
            old = listSteps[i].y;
            n = listSteps[i].x;

            arrow = new Arrow(2, Color.BLUE);
            arrow.setStartX(lines[old][n].getP1().x);
            arrow.setStartY(lines[old][n].getP1().y);
            arrow.setEndX(lines[old][n].getP2().x);
            arrow.setEndY(lines[old][n].getP2().y);

            root.getChildren().add(arrow);
            paneDraw.setContent(root);
        }

        //
        old = listSteps[countSteps].y;
        n = listSteps[countSteps].x;

        arrow = new Arrow(2, Color.YELLOWGREEN);
        arrow.setStartX(lines[old][n].getP1().x);
        arrow.setStartY(lines[old][n].getP1().y);
        arrow.setEndX(lines[old][n].getP2().x);
        arrow.setEndY(lines[old][n].getP2().y);

        root.getChildren().add(arrow);
        paneDraw.setContent(root);

        //
        if(isDone) {
            old = dest;
            for (int l : listPath) {

                if (l == dest) {
                    continue;
                }

                System.out.println("listPath: " + l);
                System.out.println(lines[l][old].toString());

                //draw line
                arrow = new Arrow(2, Color.GREEN);
                arrow.setStartX(lines[l][old].getP1().x);
                arrow.setStartY(lines[l][old].getP1().y);
                arrow.setEndX(lines[l][old].getP2().x);
                arrow.setEndY(lines[l][old].getP2().y);

                root.getChildren().add(arrow);
                paneDraw.setContent(root);
                old = l;
            }
        }
    }

    // draw path primary
    public void drawPath() {
        int old;
        Arrow arrow;
        if(isDone) {
            old = dest;
            for (int l : listPath) {

                if (l == dest) {
                    continue;
                }
                System.out.println("listPath: " + l);
                System.out.println(lines[l][old].toString());
                //draw line

                arrow = new Arrow(2, Color.GREEN);
                arrow.setStartX(lines[l][old].getP1().x);
                arrow.setStartY(lines[l][old].getP1().y);
                arrow.setEndX(lines[l][old].getP2().x);
                arrow.setEndY(lines[l][old].getP2().y);

                root.getChildren().add(arrow);
                paneDraw.setContent(root);
                old = l;
            }
        }
    }

    // algorithm dijkstra
    public boolean Dijkstra() {
        boolean[] mark = new boolean[MAX_POINTS];

        for (int i = 0; i < countPoint; i++) {
            mark[i] = false;
            newDistance[i] = 0;
        }

        countSteps = maxSteps = 0;
        listPath.clear();

        PriorityQueue<Element> queue = new PriorityQueue<Element>(countPoint, new Element());
        queue.add(new Element(start, 0, null));
        isFounded = false;
        Element e;

        while (queue.size() > 0) {
            e = queue.poll();
            queue.remove(e);

            if (e.distance != newDistance[e.n]) {
                continue;
            }

            //System.out.println("(" + points[e.n].x + ", " + points[e.n].y + ")");
            System.out.println(e.n);

            mark[e.n] = true;

            if (e.prev != null) {
                listSteps[maxSteps] = new Point(e.n, e.prev.n);
                maxSteps++;
            }

            if (e.n == dest) {
                int nodes = 0;
                while (true) {
                    listPath.add(e.n);
                    e = e.prev;
                    if (e == null) break;
                    nodes++;
                }
                lblSteps.setText("Steps: " + maxSteps);
                lblNodes.setText("Nodes: " + nodes);
                isFounded = true;
                break;
            }

            for (int i = 0; i < countPoint; i++) {
                if (!mark[i] && distance[e.n][i] > 0) {
                    // e.distance: distance toi dinh dang xet
                    // distance[i][j]: distance i->j
                    int dis = e.distance + distance[e.n][i];

                    if (newDistance[i] == 0) {
                        newDistance[i] = dis;
                        queue.add(new Element(i, dis, e));
                    } else {
                        if (newDistance[i] > dis) {
                            newDistance[i] = dis;
                            queue.add(new Element(i, dis, e));
                        }
                    }
                }
            }
        }

        return isFounded;
    }

    public void generate() {
        int array[] = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 };
        int count = MAX_POINTS;
        int k = 0, n, region;

        Point[] listLines = new Point[MAX_LINES];

        int offsetX, offsetY, px1, px2, py1, py2;

        System.out.println("Count point: " + countPoint);
        System.out.println("Count line: " + countLine);

        for (int i = 0; i < countPoint; i++) {
            for (int j = 0; j < countPoint; j++) {
                distance[i][j] = 0;
                if (i != j) {
                    listLines[k] = new Point(i, j);
                    k++;
                }
            }

            n = rd.nextInt(count);
            region = array[n];
            count--;
            array[n] = array[count];
            p = randomInRegion(region);
            points[i] = p;
        }

        for (int i = 0; i < countLine; i++) {
            n = rd.nextInt(k);
            p = listLines[n];
            k--;

            listLines[n] = listLines[k];
            distance[p.x][p.y] = calDistance(points[p.x], points[p.y]);

            px1 = points[p.x].x;
            px2 = points[p.y].x;
            py1 = points[p.x].y;
            py2 = points[p.y].y;

            offsetX = (px2 - px1) * RADIUS3 / distance[p.x][p.y];
            offsetY = (py2 - py1) * RADIUS3 / distance[p.x][p.y];

            lines[p.x][p.y] = new PairPoint(
                    new Point(px1 + offsetX, py1 + offsetY),
                    new Point(px2 - offsetX, py2 - offsetY));
        }

        root = new Pane();
        Arrow arrow;

        // draw line + arrow
        for (int i = 0; i < countPoint; i++) {
            for (int j = 0; j < countPoint; j++) {
                if (distance[i][j] != 0) {
                    arrow = new Arrow();

                    arrow.setEndX(lines[i][j].getP2().x);
                    arrow.setEndY(lines[i][j].getP2().y);
                    arrow.setStartX(lines[i][j].getP1().x);
                    arrow.setStartY(lines[i][j].getP1().y);

                    root.getChildren().add(arrow);
                }
            }
        }

        // draw node + number node
        for (int i = 0; i < countPoint; i++) {
            p = points[i];

            Circle circle = new Circle(p.x, p.y, RADIUS);
            circle.setFill(Color.GRAY);
            circle.setOpacity(0.7);

            System.out.println(i + ". (" + p.x + "," + p.y + ")");
            //System.out.println("Cicle " + i + ". (" + circle.getCenterX() + "," + circle.getCenterY() + ")");

            Text text = new Text("" + i);
            text.setFont(Font.font(17));
            text.setFill(Color.BLACK);

            circle.setStroke(Color.RED);

            text.layoutXProperty().bind(circle.centerXProperty().add(-text.getLayoutBounds().getWidth() / 2));
            text.layoutYProperty().bind(circle.centerYProperty().add(5));
            nodes.add(circle);
            root.getChildren().addAll(circle, text);
        }

        // add graph to Pane
        paneDraw.setContent(root);
    }

    public static int calDistance(Point p1, Point p2) {
        float x = p1.x - p2.x;
        float y = p1.y - p2.y;

        return (int) (Math.sqrt(x * x + y * y));
    }

    public Point randomInRegion(int region) {
        int min_X = region % 5 * SIZE + RADIUS;
        int max_X = min_X + SIZE - RADIUS2;
        int min_Y = region / 5 * SIZE + RADIUS;
        int max_Y = min_Y + SIZE - RADIUS2;

        return new Point(min_X + rd.nextInt(max_X - min_X + 1), min_Y + rd.nextInt(max_Y - min_Y + 1));
    }

    public void reset() {
        start = dest = -1;
        countSteps = maxSteps = 0;
        listPath.clear();
        nodes.clear();

        chooseState = 0;
        lblStatus.setText(textChoose[chooseState]);

        lblPointStart.setText("Point start: ");
        lblPointEnd.setText("Point end: ");
        lblSteps.setText("Steps: ");
        lblNodes.setText("Nodes: ");

        isFounded = false;
        isDone = false;
    }

    public void AlertWarning(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public Pair<Circle, Text> drawCircle(Point p, int i, Color c) {
        Pair<Circle, Text> pair;
        Circle circle = new Circle(p.x, p.y, RADIUS);
        circle.setFill(c);
        circle.setOpacity(0.7);

        Text text = new Text("" + i);
        text.setFont(Font.font(17));
        text.setFill(Color.BLACK);

        circle.setStroke(Color.RED);

        text.layoutXProperty().bind(circle.centerXProperty().add(-text.getLayoutBounds().getWidth() / 2));
        text.layoutYProperty().bind(circle.centerYProperty().add(5));

        pair = new Pair<Circle, Text>(circle, text);
        return pair;
    }

    public void repaintGraph() {
        root = new Pane();
        Arrow arrow;

        // draw line + arrow
        for (int i = 0; i < countPoint; i++) {
            for (int j = 0; j < countPoint; j++) {
                if (distance[i][j] != 0) {
                    arrow = new Arrow();

                    arrow.setEndX(lines[i][j].getP2().x);
                    arrow.setEndY(lines[i][j].getP2().y);
                    arrow.setStartX(lines[i][j].getP1().x);
                    arrow.setStartY(lines[i][j].getP1().y);

                    root.getChildren().add(arrow);
                }
            }
        }

        // draw node + number node
        for (int i = 0; i < countPoint; i++) {
            p = points[i];

            Circle circle = new Circle(p.x, p.y, RADIUS);
            circle.setFill(Color.GRAY);
            circle.setOpacity(0.7);

            System.out.println(i + ". (" + p.x + "," + p.y + ")");

            Text text = new Text("" + i);
            text.setFont(Font.font(17));
            text.setFill(Color.BLACK);

            circle.setStroke(Color.RED);

            text.layoutXProperty().bind(circle.centerXProperty().add(-text.getLayoutBounds().getWidth() / 2));
            text.layoutYProperty().bind(circle.centerYProperty().add(5));
            nodes.add(circle);
            root.getChildren().addAll(circle, text);
        }

        // add graph to Pane
        paneDraw.setContent(root);
    }
}
