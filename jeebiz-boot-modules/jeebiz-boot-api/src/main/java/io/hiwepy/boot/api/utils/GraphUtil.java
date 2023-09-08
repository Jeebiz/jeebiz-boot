package io.hiwepy.boot.api.utils;


import com.google.common.collect.ImmutableList;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

import java.util.*;

public class GraphUtil {

    public static <T> List<T> topologicalSort(Graph<T> graph) {
        List<T> sortedNodes = new ArrayList<>();
        Set<T> visitedNodes = new HashSet<>();

        for (T node : graph.nodes()) {
            if (!visitedNodes.contains(node)) {
                depthFirstSearch(graph, node, visitedNodes, sortedNodes);
            }
        }

        return sortedNodes;
    }

    private static <T> void depthFirstSearch(Graph<T> graph, T node, Set<T> visitedNodes, List<T> sortedNodes) {
        visitedNodes.add(node);

        for (T successor : graph.successors(node)) {
            if (!visitedNodes.contains(successor)) {
                depthFirstSearch(graph, successor, visitedNodes, sortedNodes);
            }
        }

        sortedNodes.add(node);
    }

    public static void main(String[] args) {
        // 创建一个可变图对象
        MutableGraph<Long> graph = GraphBuilder.directed().build();

        Map<Long, String> nodeMap = new HashMap<>();
        nodeMap.put(1696054935894106112L, "A");
        nodeMap.put(1696054911126740992L, "B");
        nodeMap.put(1696054882664194048L, "C");
        nodeMap.put(1696054851655704576L, "D");
        nodeMap.put(1696054137244098560L, "E");
        nodeMap.put(1696054137244098561L, "F");

        // 添加节点
        graph.addNode(1696054935894106112L); // A
        graph.addNode(1696054911126740992L); // B
        graph.addNode(1696054882664194048L); // C
        graph.addNode(1696054851655704576L); // D
        graph.addNode(1696054137244098560L); // E
        graph.addNode(1696054137244098561L); // F
        // 添加边
        // A->B
        graph.putEdge(1696054935894106112L, 1696054911126740992L);
        // A->C
        graph.putEdge(1696054935894106112L, 1696054882664194048L);
        // A->D
        graph.putEdge(1696054935894106112L, 1696054851655704576L);
        // A->E
        graph.putEdge(1696054935894106112L, 1696054137244098560L);
        // A->F
        graph.putEdge(1696054935894106112L, 1696054137244098561L);
        // B->D
        graph.putEdge(1696054911126740992L, 1696054851655704576L);
        // B->E
        graph.putEdge(1696054911126740992L, 1696054137244098560L);
        // C->E
        graph.putEdge(1696054882664194048L, 1696054137244098560L);
        // C->F
        graph.putEdge(1696054882664194048L, 1696054137244098561L);

        // 执行拓扑排序
        List<Long> topologicalOrder = GraphUtil.topologicalSort(graph);

        // 输出执行顺序
        System.out.println("Execution Order:");
        for (Long node : topologicalOrder) {
            System.out.println("Node: " + node + ", Name: " + nodeMap.get(node));
        }

        // 遍历图形
        /*for (Long node : graph.nodes()) {
            System.out.println("Node: " + node);
            for (Long neighbor : graph.adjacentNodes(node)) {
                System.out.println("Neighbor: " + neighbor);
            }
        }*/
    }

}
