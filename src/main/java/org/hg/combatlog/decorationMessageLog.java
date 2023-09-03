package org.hg.combatlog;

import java.awt.*;
import java.util.List;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

public class decorationMessageLog {

    public decorationMessageLog(List<TextComponent> messages, int page, int max_page) {
        messages.add(0, new TextComponent(ChatColor.AQUA+""+ ChatColor.BOLD+"╔═══════Yog-Sothoth═══════╗"));
        if (page <= 0 || max_page < page){
            return;
        }
        //   ╚【 |1| 2 3 4 5 6 7 ... 90 】╝
        //   ╚【 1 ... 70 71 |72| 73 74 ... 90 】╝
        //   ╚【 1 ... 85 86 87 |88| 89 90 】╝
        TextComponent str = new TextComponent(ChatColor.AQUA+""+"╚【 ");
        if (page <= 4){
            for (int i = 1; i <= 7 && i <= max_page; i++){
                TextComponent hover_page = new TextComponent(ChatColor.RESET+"");
                hoverCombatPageSet(hover_page, "/combat_page "+i);
                if (page == i){
                    hover_page.addExtra(new TextComponent(ChatColor.RESET+"|"+ChatColor.UNDERLINE+page+ChatColor.RESET+"| "));
                    hover_page.setColor(net.md_5.bungee.api.ChatColor.GOLD);
                }
                else {
                    hover_page.addExtra(new TextComponent(ChatColor.RESET+""+i+" "));
                }
                str.addExtra(hover_page);
            }
            if (max_page > 7) {
                TextComponent hover_page = new TextComponent(ChatColor.RESET+""+max_page);
                hoverCombatPageSet(hover_page, "/combat_page "+max_page);
                str.addExtra(new TextComponent(ChatColor.RESET + "... "));
                str.addExtra(hover_page);
            }
        } else if (page+2>=max_page) {
            TextComponent hover_page = new TextComponent(ChatColor.RESET+"1");
            hoverCombatPageSet(hover_page, "/combat_page 1");
            str.addExtra(hover_page);
            str.addExtra(new TextComponent(ChatColor.RESET + " ... "));
            for (int i = max_page-4; i <= max_page; i++){
                hover_page = new TextComponent(ChatColor.RESET+"");
                hoverCombatPageSet(hover_page, "/combat_page "+i);
                if (page == i){
                    hover_page.addExtra(new TextComponent(ChatColor.RESET+"|"+ChatColor.UNDERLINE+page+ChatColor.RESET+"| "));
                    hover_page.setColor(net.md_5.bungee.api.ChatColor.GOLD);
                }
                else {
                    hover_page.addExtra(new TextComponent(ChatColor.RESET+""+i+" "));
                }
                str.addExtra(hover_page);
            }
        }
        else {
            TextComponent hover_page = new TextComponent(ChatColor.RESET+"1");
            hoverCombatPageSet(hover_page, "/combat_page 1");
            str.addExtra(hover_page);
            str.addExtra(new TextComponent(ChatColor.RESET + " ... "));
            /// ...
            for (int i = page-2; i <= page + 2; i++){
                hover_page = new TextComponent(ChatColor.RESET+"");
                hoverCombatPageSet(hover_page, "/combat_page "+i);
                if (page == i){
                    hover_page.addExtra(new TextComponent(ChatColor.RESET+"|"+ChatColor.UNDERLINE+page+ChatColor.RESET+"| "));
                    hover_page.setColor(net.md_5.bungee.api.ChatColor.GOLD);
                }
                else {
                    hover_page.addExtra(new TextComponent(ChatColor.RESET+""+i+" "));
                }
                str.addExtra(hover_page);
            }
            /// ...
            hover_page = new TextComponent(ChatColor.RESET+""+max_page);
            hoverCombatPageSet(hover_page, "/combat_page "+max_page);
            str.addExtra(new TextComponent(ChatColor.RESET + "... "));
            str.addExtra(hover_page);
        }

        str.addExtra(new TextComponent(ChatColor.AQUA+"】╝"));
        messages.add(str);
        return;
    }
    private void hoverCombatPageSet(TextComponent hover_page, String name){
        hover_page.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,name));
        hover_page.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(name)));
    }
}
