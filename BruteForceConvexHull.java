import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

/**
 * 
 * @author vthai (v2.thai@connect.qut.edu.au )
 *
 */

public class BruteForceConvexHull {
    List<Point> points = new ArrayList<>();
    List<Line> lines = new ArrayList<>();
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                CartesianFrame frame = new CartesianFrame(new BruteForceConvexHull());
                frame.showUI();
            }
        });
    }

    public void findConvexHull() {
        // ax + by = c
        // a = y2 - y1
        // b = x1 - x2
        // c = x1y2 - y1x2
        // Levitin [p. 113]
        int a;
        int b;
        int c;
        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);
            System.out.print("segment a " + p1.toString());
            
            for(int j = 0; j < points.size(); j++) {
                if (j != i) {
                    Point p2 = points.get(j);
                    a = p2.y - p1.y;
                    b = p1.x - p2.x;
                    c = (p1.x*p2.y) - (p1.y*p2.x);
                    
                    System.out.println("\n\tsegment b " + p2.toString());
                    
                    Boolean greaterThanC = null;
                    boolean isFrontier = true;
                    
                    for (int k = 0; k < points.size(); k++) {
                        if (k != j && k != i) {
                            Point p = points.get(k);
                            boolean newComparision = (((a*p.x) + (b*p.y)) > c);
                            
                            System.out.println("\t\tpoint " + p.toString() + " is on side " + newComparision);
                            if(greaterThanC != null) {
                                if (newComparision != greaterThanC) {
                                    isFrontier = false;
                                    break;
                                }
                            } 
                            greaterThanC = newComparision;
                        }
                    }
                    if (isFrontier) {
                        lines.add(new Line(p1, p2));
                    }
                }
            }
        }
    }

    public void clear() {
        points.clear();
        lines.clear();
    }
} 

class Line {
    Point p1;
    Point p2;
    
    public Line(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }
}

class Point {
    int x;
    int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public String toString() {
        return x + "-" + y;
    }
}

class CartesianFrame extends JFrame {
    CartesianPanel view;
    BruteForceConvexHull model;

    public CartesianFrame(BruteForceConvexHull model) {
        view = new CartesianPanel(model);
        ClickEventListener clickEventListener = new ClickEventListener(model, view);
        view.addMouseListener(clickEventListener);
        add(view);
    }

    public void showUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Convex Hull");
        setSize(700, 700);
        setVisible(true);
    }
}

class ClickEventListener extends MouseInputAdapter {
    BruteForceConvexHull model;
    CartesianPanel view;
    
    public ClickEventListener(BruteForceConvexHull model, CartesianPanel view) {
        this.model = model;
        this.view = view;
    }
    public void mouseClicked(MouseEvent me) {
        if (SwingUtilities.isLeftMouseButton(me)) {
            System.out.println(me.getX() + "-" + me.getY());
            view.update(me.getX(), me.getY());
        }
        else if (SwingUtilities.isRightMouseButton(me)) {
            view.drawConvexHull();
        }
    }
}

class CartesianPanel extends JPanel {
    BruteForceConvexHull model;
    boolean drawConvex = false;
    
    public CartesianPanel(BruteForceConvexHull model) {
        this.model = model;
    }
    
    public void drawConvexHull() {
        if (!drawConvex) {
            model.findConvexHull();
            drawConvex = true;
        } else {
            model.clear();
            drawConvex = false;
        }
        
        repaint();
    }

    public void update(int x, int y) {
        drawConvex = false;
        model.points.add(new Point(x, y));
        
        repaint();
    }
    
    public void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLUE);
        for(int i = 0; i < model.points.size(); i++) {
            g2.drawOval(model.points.get(i).x, model.points.get(i).y, 3, 3);
            //g2.drawLine(points[i].x, points[i].y, points[i].x, points[i].y);
        }
        
        if (drawConvex) {
            for (Line line : model.lines) {
                g2.drawLine(line.p1.x, line.p1.y, line.p2.x, line.p2.y);
            }
        }
    }
}
