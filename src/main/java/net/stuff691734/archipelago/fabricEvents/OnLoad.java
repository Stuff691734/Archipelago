package net.stuff691734.archipelago.fabricEvents;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class OnLoad {
    private OnLoad() {
    }

    /*
        This is taken straight out of fabric api's newer versions
        it is not in older versions and I didn't want to figure it out myself
     */
    public static final Event<OnLoad.Load> ENTITY_LOAD = EventFactory.createArrayBacked(OnLoad.Load.class, callbacks -> (entity, world) -> {
        if (EventFactory.isProfilingEnabled()) {
            final Profiler profiler = world.getProfiler();
            profiler.push("fabricServerEntityLoad");

            for (OnLoad.Load callback : callbacks) {
                profiler.push(EventFactory.getHandlerName(callback));
                callback.onLoad(entity, world);
                profiler.pop();
            }

            profiler.pop();
        } else {
            for (OnLoad.Load callback : callbacks) {
                callback.onLoad(entity, world);
            }
        }
    });

    public interface Load {
        void onLoad(Entity entity, ServerWorld world);
    }
}