package repoll.client.ui;

import org.jetbrains.annotations.NotNull;
import repoll.models.Answer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PollResultsVisualization {
    private static final Logger LOG = Logger.getLogger(PollResultsVisualization.class.getName());
    private JList<Answer> legendList;
    private JPanel rootPanel;
    private JPanel chartPanel;

    private final List<Answer> answers;
    private Answer selectedAnswer;
    private final DefaultListModel<Answer> legendListModel;
    private Map<Answer, Color> colorMap = new IdentityHashMap<>();

    public PollResultsVisualization(List<Answer> answers) {
        this.answers = answers;
        legendListModel = new DefaultListModel<>();
        if (answers.isEmpty()) {
            legendListModel.addElement(null);
        }

        for (Answer answer : answers) {
            legendListModel.addElement(answer);
        }
        legendList.setModel(legendListModel);
        legendList.setFixedCellWidth(150);
        legendList.setCellRenderer(new ListCellRenderer<Answer>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Answer> list, Answer value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value == null) {
                    return new JLabel("No answers");
                }
                String description = null;
                description = String.format("%s: %d votes", value.getDescription(), countVotes(value));
                return new JLabel(description, new ColorFilledIcon(16, 16, colorMap.get(value)), SwingConstants.LEFT);
            }
        });
        prepareColorMap(answers);
        legendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        legendList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = legendList.locationToIndex(e.getPoint());
                selectedAnswer = legendListModel.elementAt(index);
                chartPanel.repaint();
            }
        });
        chartPanel.add(new PieChart(), BorderLayout.CENTER);
    }

    private static class ColorFilledIcon implements Icon {
        private final int width, height;
        private final Color color;

        private ColorFilledIcon(int width, int height, Color color) {
            this.width = width;
            this.height = height;
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g;
            Rectangle2D rect = new Rectangle2D.Double(x, y, width, height);
            g2d.setPaint(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(rect);
            g2d.setPaint(color);
            g2d.fill(rect);
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }

    private class PieChart extends JComponent {
        private PieChart() {
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(Color.GRAY);
            double size = Math.min(getWidth(), getHeight());
            double x = getX() + size * 0.1;
            double y = getY() + size * 0.1;
            size = size * 0.8;
            Arc2D fullCircle = new Arc2D.Double(x, y, size, size, 0, 360, Arc2D.PIE);
            g2d.fill(fullCircle);
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(fullCircle);
            int totalVotes = 0;
            for (Answer answer : answers) {
                totalVotes += countVotes(answer);
            }
            if (totalVotes != 0) {
                double startAngle = 0;
                for (Answer answer : answers) {
                    g2d.setPaint(answer == selectedAnswer ? Color.RED : colorMap.get(answer));
                    double extent = 360.0 * countVotes(answer) / totalVotes;
                    g2d.fill(new Arc2D.Double(x, y, size, size, startAngle, extent, Arc2D.PIE));
                    startAngle += extent;
                }
            }
        }
    }

    private void prepareColorMap(List<Answer> answers) {
        if (answers.isEmpty()) {
            return;
        }
        int step = 256 / answers.size();
        for (int i = 0; i < answers.size(); i++) {
            int colorComponent = 255 - i * step;
            colorMap.put(answers.get(i), new Color(colorComponent, colorComponent, colorComponent));
        }
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    private int countVotes(@NotNull Answer answer) {
        return MainApplication.getFacade().getAnswerVotes(answer).size();
    }

}