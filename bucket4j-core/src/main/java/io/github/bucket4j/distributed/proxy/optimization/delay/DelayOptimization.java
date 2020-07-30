/*
 *
 *   Copyright 2015-2017 Vladimir Bukhtoyarov
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.bucket4j.distributed.proxy.optimization.delay;

import io.github.bucket4j.TimeMeter;
import io.github.bucket4j.distributed.proxy.AsyncCommandExecutor;
import io.github.bucket4j.distributed.proxy.CommandExecutor;
import io.github.bucket4j.distributed.proxy.optimization.Optimization;
import io.github.bucket4j.distributed.proxy.optimization.DelayParameters;
import io.github.bucket4j.distributed.proxy.optimization.OptimizationListener;
import io.github.bucket4j.distributed.proxy.optimization.batch.AsyncBatchingExecutor;
import io.github.bucket4j.distributed.proxy.optimization.batch.BatchingExecutor;

public class DelayOptimization implements Optimization {

    private final DelayParameters delayParameters;
    private final OptimizationListener listener;
    private final TimeMeter timeMeter;

    public DelayOptimization(DelayParameters delayParameters, OptimizationListener listener, TimeMeter timeMeter) {
        this.delayParameters = delayParameters;
        this.timeMeter = timeMeter;
        this.listener = listener;
    }

    @Override
    public Optimization withListener(OptimizationListener listener) {
        return new DelayOptimization(delayParameters, listener, timeMeter);
    }

    @Override
    public CommandExecutor apply(CommandExecutor originalExecutor) {
        DelayedCommandExecutor predictiveCommandExecutor = new DelayedCommandExecutor(originalExecutor, delayParameters, listener, timeMeter);
        return new BatchingExecutor(predictiveCommandExecutor, listener);
    }

    @Override
    public AsyncCommandExecutor apply(AsyncCommandExecutor originalExecutor) {
        DelayedCommandExecutor predictiveCommandExecutor = new DelayedCommandExecutor(originalExecutor, delayParameters, listener, timeMeter);
        return new AsyncBatchingExecutor(predictiveCommandExecutor, listener);
    }

}
