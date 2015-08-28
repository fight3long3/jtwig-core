package org.jtwig.functions.resolver.position;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.jtwig.functions.FunctionArgument;
import org.jtwig.functions.resolver.position.vararg.FromPositionExtractor;
import org.jtwig.functions.resolver.position.vararg.FunctionArgumentMerger;
import org.jtwig.reflection.input.InputParameterResolverContext;
import org.jtwig.reflection.model.Value;
import org.jtwig.reflection.model.java.JavaMethodArgument;

import java.util.List;

public class VarArgsPositionParameterResolver implements PositionParameterResolver {
    private final FunctionArgumentMerger functionArgumentMerger;
    private final FromPositionExtractor fromPositionExtractor;

    public VarArgsPositionParameterResolver(FunctionArgumentMerger functionArgumentMerger, FromPositionExtractor fromPositionExtractor) {
        this.functionArgumentMerger = functionArgumentMerger;
        this.fromPositionExtractor = fromPositionExtractor;
    }

    @Override
    public Optional<Value> resolve(JavaMethodArgument javaMethodArgument, int position, InputParameterResolverContext<FunctionArgument> context, Class to) {
        if (javaMethodArgument.isVarArg()) {
            if (context.size() <= position) return Optional.of(new Value(null));
            return fromPositionExtractor
                    .extract(position, context)
                    .transform(getFunction())
                    .or(Optional.<Value>absent());
        } else {
            return Optional.absent();
        }
    }

    private Function<List<FunctionArgument>, Optional<Value>> getFunction() {
        return new Function<List<FunctionArgument>, Optional<Value>>() {
            @Override
            public Optional<Value> apply(List<FunctionArgument> input) {
                return functionArgumentMerger.merge(input);
            }
        };
    }
}