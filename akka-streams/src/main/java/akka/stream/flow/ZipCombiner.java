package akka.stream.flow;

import akka.NotUsed;
import akka.japi.Pair;
import akka.stream.FanInShape2;
import akka.stream.Outlet;
import akka.stream.SourceShape;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Zip;

/**
 * @author by constantin on 6/27/17.
 */
public class ZipCombiner<I, O, MAT> {

    public Source<Pair<I, O>, NotUsed> createFlow(Source<I, MAT> source1, Source<I, MAT> source2) {
        return Source.fromGraph(
                GraphDSL.create(
                        builder -> {
                            final FanInShape2<I, O, Pair<I, O>> zip = builder.add(Zip.<I, O>create());

                            final Outlet<I> input0 = builder.add(source1).out();
                            final Outlet<I> input1 = builder.add(source2).out();
                            builder.from(input0).toInlet(zip.in0());
                            builder.from(input1).toInlet(zip.in1());

                            return SourceShape.apply(zip.out());
                        }));
    }

}
