package net.stuff691734.archipelago;


import java.util.Arrays;
import java.util.List;

public class SlotData {
    public String unlock_type;
    public String final_goal;
    public List<String> activated_modules;
    public List<String> advancement_difficulty;
    public List<String> ftb_quest_shape;
    public boolean advancement_checks_give_items;
    public boolean quest_checks_give_rewards;
    public boolean death_link;
    public boolean roots_unlocked;

    public boolean isInitiated = false;

    public SlotData(
            String unlock_type,
            String final_goal,
            String activated_modules,
            String advancement_difficulty,
            String ftb_quest_shape,
            String advancement_checks_give_items,
            String quest_checks_give_rewards,
            String death_link,
            String roots_unlocked
    ) {
        this.unlock_type = unlock_type;
        this.final_goal = final_goal;
        this.activated_modules = Arrays.stream(activated_modules.split("\\|")).toList();
        this.advancement_difficulty = Arrays.stream(advancement_difficulty.split("\\|")).toList();
        this.ftb_quest_shape = Arrays.stream(ftb_quest_shape.split("\\|")).toList();
        this.advancement_checks_give_items = advancement_checks_give_items.equals("1");
        this.quest_checks_give_rewards = quest_checks_give_rewards.equals("1");
        this.death_link = death_link.equals("1");
        this.roots_unlocked = roots_unlocked.equals("1");

        this.isInitiated = true;
    }

    public SlotData() {

    }

    public boolean isFTBQuestRewardRandomized(String shape) {
        // default: on
        return !this.isInitiated ||
                (
                    this.activated_modules.contains("FTBQuests") &&
                    this.ftb_quest_shape.contains(shape) &&
                    this.quest_checks_give_rewards
                );
    }

    public boolean isCheckFinalGoal(String checkName) {
        return this.isInitiated && checkName.equals(String.format("%s %s", (Object[]) this.final_goal.split(" ")));
    }
}
