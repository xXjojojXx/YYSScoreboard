package com.github.euonmyoji.yysscoreboard.data;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.text.Text;

import static com.github.euonmyoji.yysscoreboard.YysScoreBoard.textManager;

/**
 * @author yinyangshi
 */
public class TabData {
    public final int delay;
    private final String header;
    private final String footer;
    private final String prefix;
    private final String suffix;

    public TabData(CommentedConfigurationNode node, int defDelay) {
        header = node.getNode("header").getString("");
        footer = node.getNode("footer").getString("");
        prefix = node.getNode("prefix").getString("");
        suffix = node.getNode("suffix").getString("");
        this.delay = node.getNode("delay").getInt(defDelay);
    }

    public void setTab(Player p) {
        TabList tabList = p.getTabList();
        tabList.setHeaderAndFooter(textManager.toText(header, p), textManager.toText(footer, p));
        Text displayName = textManager.toText(prefix, p).toBuilder()
                .append(p.getDisplayNameData().displayName().get())
                .append(textManager.toText(suffix, p))
                .build();

        Sponge.getServer().getOnlinePlayers().forEach(player -> player
                .getTabList().getEntry(p.getUniqueId()).ifPresent(entry -> entry.setDisplayName(displayName)));
    }
}
