import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class uses <a href="https://visjs.org/">VisJS</a> to generate a network of nodes and edges.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 */
public class VisJS {

    private String divID = "mynetwork", visJsFile = "http://yanwittmann.de/projects/vis/vis.js";
    private String visCssFile = "http://yanwittmann.de/projects/vis/vis.css";
    private int width = 1200, height = 800;

    public VisJS(String divID, int width, int height) {
        this.divID = divID;
        this.width = width;
        this.height = height;
    }

    public VisJS() {
    }

    public void setCssFile(String path) {
        this.visCssFile = path;
    }

    public void setJsFile(String path) {
        this.visJsFile = path;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setDivID(String divID) {
        this.divID = divID;
    }

    private final ArrayList<VisNode> nodes = new ArrayList<>();
    private final ArrayList<VisEdge> edges = new ArrayList<>();

    public void addNode(String title, int type) {
        if (title.length() == 0) return;
        for (VisNode node : nodes)
            if (node.getName().equals(title) && node.getType() == type) return;
        nodes.add(new VisNode(title, type));
    }

    private String getNodes() {
        StringBuilder stringBuilder = new StringBuilder();
        for (VisNode node : nodes) {
            if (stringBuilder.length() > 0) stringBuilder.append(",\n");
            stringBuilder.append(node.getNodeText());
        }
        return stringBuilder.toString();
    }

    public void addEdge(String from, String to) {
        addEdge(from, to, "", false);
    }

    public void addEdgeBoth(String from, String to) {
        addEdge(from, to, "", false);
        addEdge(to, from, "", false);
    }

    public boolean addEdge(String from, String to, String label) {
        return addEdge(from, to, label, false);
    }

    public boolean addEdge(String from, String to, String label, boolean labelHoverRequired) {
        if (from.length() == 0 || to.length() == 0) return false;
        VisNode node1 = getNodeByName(from);
        VisNode node2 = getNodeByName(to);
        if (node1 == null || node2 == null) return false;

        for (VisEdge edge : edges)
            if (edge.isEdge(node1.getID(), node2.getID())) {
                edge.addLabel(label);
                return true;
            }
        edges.add(new VisEdge(node1.getID(), node2.getID(), label.replace("\"", "\\\""), labelHoverRequired));
        return true;
    }

    private VisNode getNodeByName(String name) {
        for (VisNode node : nodes) {
            if (node.getName().equals(name)) return node;
        }
        return null;
    }

    private String getEdges() {
        StringBuilder stringBuilder = new StringBuilder();
        for (VisEdge edge : edges) {
            if (stringBuilder.length() > 0) stringBuilder.append(",\n");
            stringBuilder.append(edge.getEdgeText());
        }
        return stringBuilder.toString();
    }

    public static class VisNode {
        private final String name;
        private final int type;
        private final int id;
        private static int id_counter = 0;

        public VisNode(String name, int type) {
            this.name = name;
            this.type = type;
            id = id_counter;
            id_counter++;
        }

        public String getName() {
            return name;
        }

        public int getType() {
            return type;
        }

        public String getNodeText() {
            if (!types.containsKey(type))
                return "{id: " + id + ", label: \"" + name + "\", shape: 'ellipse', color: '#b0ffff'}";
            return "{id: " + id + ", label: \"" + name + "\"" + types.get(type);
        }

        public int getID() {
            return id;
        }
    }

    public static final String LABEL_ALWAYS_VISIBLE = "label";
    public static final String LABEL_HOVER_VISIBLE = "title";

    public static class VisEdge {
        private final int from, to;
        private final LineBuilder label;
        private final boolean labelHoverRequired;

        public VisEdge(int from, int to, String label, boolean labelHoverRequired) {
            this.from = from;
            this.to = to;
            this.label = new LineBuilder(label);
            this.labelHoverRequired = labelHoverRequired;
        }

        public void addLabel(String label) {
            this.label.append(label);
        }

        public boolean isEdge(int from, int to) {
            return from == this.from && to == this.to;
        }

        public String getEdgeText() {
            if (label.length() > 0) {
                if (labelHoverRequired) {
                    label.setLinebreakSymbol("<br>");
                    return "{from: " + from + ", to: " + to + ", " + LABEL_HOVER_VISIBLE + ": \"" + label.toString() + "\"}";
                } else {
                    label.setLinebreakSymbol("\\n");
                    return "{from: " + from + ", to: " + to + ", " + LABEL_ALWAYS_VISIBLE + ": \"" + label.toString() + "\"}";
                }
            }
            return "{from: " + from + ", to: " + to + "}";
        }
    }

    private final static HashMap<Integer, String> types = new HashMap<>();

    public void addNodeType(int typeID, String... parameters) {
        StringBuilder sb = new StringBuilder();
        for (String parameter : parameters)
            sb.append(", ").append(parameter);
        sb.append("}");
        types.put(typeID, sb.toString());
    }

    public void addNodeType(int typeID, NodeTypeBuilder parameters) {
        types.put(typeID, parameters.getParameters());
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public int getEdgeCount() {
        return edges.size();
    }

    public ArrayList<String> generate() {
        ArrayList<String> generated = new ArrayList<>();

        generated.add("<script type=\"text/javascript\" src=\"" + visJsFile + "\"></script>");
        generated.add("<link href=\"" + visCssFile + "\" rel=\"stylesheet\" type=\"text/css\"/>");
        generated.add("<style type=\"text/css\">");
        generated.add("  #" + divID + " {");
        generated.add("  width: " + width + "px;");
        generated.add("  height: " + height + "px;");
        generated.add("  border: 1px solid lightgray;");
        generated.add("}");
        generated.add("</style>");

        generated.add("<div id=\"" + divID + "\"></div>");
        generated.add("<script type=\"text/javascript\">");
        generated.add("  var nodes = new vis.DataSet([");
        generated.add(getNodes());
        generated.add("  ]);");
        generated.add("  var edges = new vis.DataSet([");
        generated.add(getEdges());
        generated.add("  ]);");
        generated.add("  var container = document.getElementById('" + divID + "');");
        generated.add("  var data = {");
        generated.add("   nodes: nodes,");
        generated.add("   edges: edges");
        generated.add("  };");
        generated.add("  var options = {");
        generated.add(getOptions());
        generated.add("}");
        generated.add("var network = new vis.Network(container, data, options);");
        generated.add("</script>");

        return generated;
    }

    public static class NodeTypeBuilder {
        private final ArrayList<String> parameters = new ArrayList<>();

        public NodeTypeBuilder addParameter(String parameter) {
            parameters.add(parameter);
            return this;
        }

        public NodeTypeBuilder setColor(String color) {
            parameters.add("color: '#" + color.replace("#", "") + "'");
            return this;
        }

        public NodeTypeBuilder setShape(String shape) {
            parameters.add("shape: '" + shape + "'");
            return this;
        }

        public NodeTypeBuilder setSize(int size) {
            parameters.add("size: " + size);
            return this;
        }

        public NodeTypeBuilder setTitle(String title) {
            parameters.add("title: '" + title + "'");
            return this;
        }

        public NodeTypeBuilder setLabel(String label) {
            parameters.add("label: '" + label + "'");
            return this;
        }

        public NodeTypeBuilder setShadow(String color, int size) {
            parameters.add("shadow: {enabled: true, size: " + size + ", color: '" + color + "'}");
            return this;
        }

        public NodeTypeBuilder setShadow(int size) {
            parameters.add("shadow: {enabled: true, size: " + size + "}");
            return this;
        }

        public NodeTypeBuilder setPhysics(boolean physics) {
            parameters.add("physics: " + physics);
            return this;
        }

        public NodeTypeBuilder setHidden(boolean hidden) {
            parameters.add("hidden: " + hidden);
            return this;
        }

        public NodeTypeBuilder setMass(int mass) {
            parameters.add("mass: " + mass);
            return this;
        }

        public NodeTypeBuilder setBorderWidth(int borderWidth) {
            parameters.add("borderWidth: " + borderWidth);
            return this;
        }

        public NodeTypeBuilder setBorderWidthSelected(int borderWidthSelected) {
            parameters.add("borderWidthSelected: " + borderWidthSelected);
            return this;
        }

        public String getParameters() {
            StringBuilder sb = new StringBuilder();
            for (String parameter : parameters)
                sb.append(", ").append(parameter);
            sb.append("}");
            return sb.toString();
        }

        public final static String SHAPE_ELLIPSE = "ellipse";
        public final static String SHAPE_CIRCLE = "circle";
        public final static String SHAPE_DATABASE = "database";
        public final static String SHAPE_BOX = "box";
        public final static String SHAPE_TEXT_ONLY = "text";
        public final static String SHAPE_DIAMOND = "diamond";
        public final static String SHAPE_DOT = "dot";
        public final static String SHAPE_STAR = "star";
        public final static String SHAPE_TRIANGLE = "triangle";
        public final static String SHAPE_TRIANGLE_DOWN = "triangleDown";
        public final static String SHAPE_HEXAGON = "hexagon";
        public final static String SHAPE_SQUARE = "square";
    }

    private final ArrayList<String> options = new ArrayList<>();

    public void setOption(String option, String value) {
        options.add(option + ":" + value);
    }

    public void setOption(String option, boolean value) {
        options.add(option + ":" + value);
    }

    public void setOption(String option, int value) {
        options.add(option + ":" + value);
    }

    public void setOption(String option, double value) {
        options.add(option + ":" + value);
    }

    private String getOptions() {
        LineBuilder edges = new LineBuilder();
        LineBuilder nodes = new LineBuilder();
        LineBuilder physics = new LineBuilder();
        LineBuilder solverSettings = new LineBuilder();
        String solver = "";

        for (String option : options) {
            String[] splitted = option.split(":", 2);

            if (splitted[0].equals(EDGE_ARROW_DIRECTION))
                edges.append(EDGE_ARROW_DIRECTION + ": '" + splitted[1] + "',");
            if (splitted[0].equals(EDGE_LENGTH))
                edges.append(EDGE_LENGTH + ": " + splitted[1] + ",");
            if (splitted[0].equals(EDGE_DASHES))
                edges.append(EDGE_DASHES + ": " + splitted[1] + ",");
            if (splitted[0].equals(EDGE_SHADOW))
                edges.append(EDGE_SHADOW + ": " + splitted[1] + ",");
            if (splitted[0].equals(EDGE_SMOOTH))
                edges.append(EDGE_SMOOTH + ": " + splitted[1] + ",");
            if (splitted[0].equals(EDGE_WIDTH))
                edges.append(EDGE_WIDTH + ": " + splitted[1] + ",");

            if (splitted[0].equals(PHYSICS_SOLVER)) {
                physics.append(PHYSICS_SOLVER + ": '" + splitted[1] + "',");
                solver = splitted[1];
            }

            if (splitted[0].equals(PHYSICS_SOLVER_SETTING_NODE_DISTANCE))
                solverSettings.append(PHYSICS_SOLVER_SETTING_NODE_DISTANCE + ": " + splitted[1] + ",");
            if (splitted[0].equals(PHYSICS_SOLVER_SETTING_CENTRAL_GRAVITY))
                solverSettings.append(PHYSICS_SOLVER_SETTING_CENTRAL_GRAVITY + ": " + splitted[1] + ",");
            if (splitted[0].equals(PHYSICS_SOLVER_SETTING_SPRING_LENGTH))
                solverSettings.append(PHYSICS_SOLVER_SETTING_SPRING_LENGTH + ": " + splitted[1] + ",");
            if (splitted[0].equals(PHYSICS_SOLVER_SETTING_SPRING_CONSTANT))
                solverSettings.append(PHYSICS_SOLVER_SETTING_SPRING_CONSTANT + ": " + splitted[1] + ",");
            if (splitted[0].equals(PHYSICS_SOLVER_SETTING_DAMPING))
                solverSettings.append(PHYSICS_SOLVER_SETTING_DAMPING + ": " + splitted[1] + ",");
            if (splitted[0].equals(PHYSICS_SOLVER_SETTING_AVOID_OVERLAP))
                solverSettings.append(PHYSICS_SOLVER_SETTING_AVOID_OVERLAP + ": " + splitted[1] + ",");
        }

        LineBuilder options = new LineBuilder();
        if (edges.length() > 0) {
            options.append("edges:{");
            options.append(edges.toLines());
            options.append("},");
        }
        if (nodes.length() > 0) {
            options.append("nodes:{");
            options.append(nodes.toLines());
            options.append("},");
        }
        if (physics.length() > 0) {
            options.append("physics:{");
            options.append(physics.toLines());
            if (solverSettings.length() > 0) {
                options.append(solver + ":{");
                options.append(solverSettings.toLines());
                options.append("},");
            }
            options.append("},");
        }

        return options.toString();
    }

    /**
     * View <a href="https://visjs.github.io/vis-network/docs/network/edges.html">this site</a> for more information about the edges
     */
    public final static String EDGE_ARROW_DIRECTION = "arrows";
    public final static String EDGE_ARROW_TO = "to";
    public final static String EDGE_ARROW_FROM = "from";
    public final static String EDGE_ARROW_TO_FROM = "to;from";
    public final static String EDGE_LENGTH = "length";
    public final static String EDGE_DASHES = "dashes";
    public final static String EDGE_SHADOW = "shadow";
    public final static String EDGE_SMOOTH = "smooth";
    public final static String EDGE_WIDTH = "width";

    /**
     * View <a href="https://visjs.github.io/vis-network/docs/network/physics.html">this site</a> for more information about the solvers
     */
    public final static String PHYSICS_SOLVER = "solver";
    public final static String PHYSICS_SOLVER_REPULSION = "repulsion";
    public final static String PHYSICS_SOLVER_HIERARCHICAL_REPULSION = "hierarchicalRepulsion";
    public final static String PHYSICS_SOLVER_BARNES_HUT = "barnesHut";
    public final static String PHYSICS_SOLVER_FORCE_ATLAS_2_BASED = "forceAtlas2Based";

    public final static String PHYSICS_SOLVER_SETTING_NODE_DISTANCE = "nodeDistance";
    public final static String PHYSICS_SOLVER_SETTING_CENTRAL_GRAVITY = "centralGravity";
    public final static String PHYSICS_SOLVER_SETTING_SPRING_LENGTH = "springLength";
    public final static String PHYSICS_SOLVER_SETTING_SPRING_CONSTANT = "springConstant";
    public final static String PHYSICS_SOLVER_SETTING_DAMPING = "damping";
    public final static String PHYSICS_SOLVER_SETTING_AVOID_OVERLAP = "avoidOverlap";
}