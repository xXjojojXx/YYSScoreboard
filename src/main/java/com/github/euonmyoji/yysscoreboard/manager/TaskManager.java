package com.github.euonmyoji.yysscoreboard.manager;

import com.github.euonmyoji.yysscoreboard.YysScoreBoard;
import com.github.euonmyoji.yysscoreboard.configuration.PlayerConfig;
import com.github.euonmyoji.yysscoreboard.task.DisplayNumber;
import com.github.euonmyoji.yysscoreboard.task.DisplayObjective;
import com.github.euonmyoji.yysscoreboard.task.DisplayTab;
import com.github.euonmyoji.yysscoreboard.task.IDisplayTask;
import com.github.euonmyoji.yysscoreboard.util.Pair;
import com.github.euonmyoji.yysscoreboard.util.Util;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author yinyangshi
 */
public class TaskManager {
    public static HashMap<UUID, Pair<String, String>> usingCache = new HashMap<>();
    public static HashMap<String, DisplayObjective> objectives = new HashMap<>();
    public static HashMap<String, DisplayTab> tabs = new HashMap<>();
    private static DisplayNumber displayNumber;

    public static void registerTask(String id, DisplayObjective objective) {
        IDisplayTask task = objectives.put(id, objective);
        if (task != null) {
            task.cancel();
        }
        objective.start();

    }

    public static void registerTask(String id, DisplayTab tab) {
        IDisplayTask task = tabs.get(id);
        if (task != null) {
            task.cancel();
        }
        tab.start();
    }

    public static void registerTask(DisplayNumber task) {
        if (displayNumber != null) {
            displayNumber.cancel();
        }
        displayNumber = task;
    }

    public static void setupPlayer(Player p) {
        Pair<String, String> pair = usingCache.get(p.getUniqueId());
        usingCache.put(p.getUniqueId(), pair);
        IDisplayTask task = objectives.get(pair.first);
        if (task != null) {
            task.setupPlayer(p);
        }
        task = tabs.get(pair.second);
        if (task != null) {
            task.setupPlayer(p);
        }
    }

    public static void clear() {
        objectives.values().forEach(IDisplayTask::cancel);
        tabs.values().forEach(IDisplayTask::cancel);
        objectives.clear();
        tabs.clear();
    }

    public static void update() {
        Util.getStream(Sponge.getServer().getOnlinePlayers()).forEach(player -> update(player, null));
    }

    public static void update(Player p, PlayerConfig pc) {
        if (pc == null) {
            try {
                pc = PlayerConfig.of(p.getUniqueId());
            } catch (IOException e) {
                YysScoreBoard.logger.warn("load player config failed", e);
                return;
            }
        }
        Pair<String, String> pair = usingCache.get(p.getUniqueId());
        if (pair == null) {
            pair = new Pair<>(pc.getDisplayObjectiveID(), pc.getDisplayTabID());
            pair.immutable = !pc.isToggle();
            usingCache.put(p.getUniqueId(), pair);
        } else {
            pair.immutable = !pc.isToggle();
            pair.first = pc.getDisplayObjectiveID();
            pair.second = pc.getDisplayTabID();
        }
        setupPlayer(p);
    }
}
