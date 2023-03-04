package batchjobdemo.partition;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;


public class ColumnRangePartitioner implements Partitioner{

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        int min = 1;
        int max = 1000;
        int targetSize = (max - min)/gridSize + 1;
        System.out.println("Target size: "+targetSize);
        Map<String, ExecutionContext> result = new HashMap<>();
        
        int number = 0;
        int start = min;
        int end = start + targetSize - 1;
        
        while(start <= max)
        {
            ExecutionContext value = new ExecutionContext();
            result.put("partition"+ number, value);        

        if(end >= max)
        {
            end = max;
        }

        value.putInt("minValue", start);
        value.putInt("maxValue",end);

        start = start + targetSize;
        end = end + targetSize;
        number++;
        }
        System.out.println("Partition Result: "+result.toString());
        return result;
    }
    
}
