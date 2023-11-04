import java.util.LinkedList;

/**
 * Created by 37919 on 2023/11/3.
 */
public class ReplayMemory<T> {
    private class Memory<T> extends LinkedList<T> {

        private int limitation;

        public Memory(int limitation) {
            this.limitation = limitation;
        }

        @Override
        public boolean add(T toadd) {
            if (size() >= limitation){
                removeFirst();
            }
            return super.add(toadd);
        }
    }
    private Memory<T> memory = new Memory<T>(100);
    private static final Object[] empty = {};
    private int limit;

    public ReplayMemory(int limit){
        memory.limitation = limit;
        this.limit = limit;
    }


    public void print_limit(){
        System.out.println(Integer.toString(memory.limitation));
    }

    public boolean add(T obj){
        return memory.add(obj);
    }

    // Sample the most recent objects in memory
    public Object[] sample(int sample_size){
        if (memory.isEmpty()) return empty;
        int dataset_size = sample_size;
        if (dataset_size > memory.size()) dataset_size = memory.size();
        Object[] samples = new Object[dataset_size];
        int size = memory.size();
        for (int i = 0; i < dataset_size; i++){
            int index = size - 1 - i;
            if (index < 0) break;
            samples[i] = memory.get(index);
        }
        return samples;
    }

    public int length(){
        return memory.size();
    }

    public Object[] toArray(){
        return memory.toArray();
    }

}
