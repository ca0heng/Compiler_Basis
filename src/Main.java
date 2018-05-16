import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        HashSet<Integer> a = new HashSet<>();
        HashSet<Integer> b = new HashSet<>();
        HashMap<Character, HashSet<Integer>> map = new HashMap<>();
        map.put('a', a);
        map.get('a').addAll(b);
        System.out.println(b.size());
    }
}





