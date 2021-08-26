import com.nway.spring.jdbc.SqlExecutorTest;
import com.nway.spring.jdbc.bean.processor.asm.AsmBeanProcessor;
import com.nway.spring.jdbc.performance.entity.Monitor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Test {

    public static void main(String[] args) throws Exception {

        System.out.println(SqlExecutorTest.class.getSimpleName());

//        new AsmBeanProcessor().dump(Monitor.class);

       /* Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {

            String input = scanner.nextLine();

            String[] consumerNoArr = Arrays.copyOfRange(input.split(" "), 1, input.split(" ").length);

            // 奇数顾客给A窗口
            LinkedList<Integer> processorA = Arrays.stream(consumerNoArr).map(Integer::parseInt).filter(e -> e % 2 != 0).sorted().collect(Collectors.toCollection(LinkedList::new));
            // 偶数顾客给B窗口
            LinkedList<Integer> processorB = Arrays.stream(consumerNoArr).map(Integer::parseInt).filter(e -> e % 2 == 0).sorted().collect(Collectors.toCollection(LinkedList::new));

            if (processorA.isEmpty()) {
                processorA.addAll(processorB);
                processorB.clear();
            }
            for (int i = 1; i <= processorA.size(); i++) {
                if (i % 2 == 0 && !processorB.isEmpty()) {
                    processorA.add(i, processorB.pop());
                }
            }
            processorA.forEach(e -> System.out.print(e + " "));
        }*/
    }


}
