package com.github.euonmyoji.yysscoreboard.task;

import com.github.euonmyoji.yysscoreboard.YysScoreBoard;
import com.github.euonmyoji.yysscoreboard.configuration.PluginConfig;
import com.github.euonmyoji.yysscoreboard.data.TabData;
import com.github.euonmyoji.yysscoreboard.manager.TaskManager;
import com.github.euonmyoji.yysscoreboard.util.Pair;
import com.github.euonmyoji.yysscoreboard.util.RandomID;
import com.github.euonmyoji.yysscoreboard.util.Util;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import java.util.List;

/**
 * @author yinyangshi
 */
public class DisplayTab implements IDisplayTask {
    private final String id;
    private final List<TabData> data;
    private final RandomID randomID;
    private int index = 0;
    private TabData cur;
    private volatile boolean running;

    public DisplayTab(String id, List<TabData> data, RandomID randomID) {
        this.id = id;
        this.data = data;
        running = !data.isEmpty();
        cur = running ? data.get(0) : null;
        this.randomID = randomID;

    }

    @Override
    public void run() {
        if (running) {
            Task.Builder builder = Task.builder().execute(this);
            try {
                builder.delayTicks(data.get(index).delay.getDelay());
                Util.getStream(Sponge.getServer().getOnlinePlayers())
                        .filter(p -> TaskManager.usingCache.get(p.getUniqueId()).first.equals(id))
                        .forEach(cur::setTab);
                if (++index >= data.size()) {
                    index = 0;
                    if (randomID != null) {
                        for (Pair<String, String> value : TaskManager.usingCache.values()) {
                            if (value.second.equals(id) && !value.immutable) {
                                value.second = randomID.getID();
                            }
                        }
                    }
                }
                if (PluginConfig.asyncTab) {
                    builder.async();
                }
            } catch (Throwable e) {
                YysScoreBoard.logger.warn("something wrong", e);
            }
            builder.submit(YysScoreBoard.plugin);
        }
    }

    @Override
    public void setupPlayer(Player p) {
        if (running) {
            cur.setTab(p);
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}
