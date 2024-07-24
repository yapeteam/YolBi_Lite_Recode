package cn.yapeteam.yolbi.module;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventKey;
import cn.yapeteam.yolbi.module.impl.combat.*;
import cn.yapeteam.yolbi.module.impl.misc.*;
import cn.yapeteam.yolbi.module.impl.movement.*;
import cn.yapeteam.yolbi.module.impl.player.ChestStealer;
import cn.yapeteam.yolbi.module.impl.player.MurdererFinder;
import cn.yapeteam.yolbi.module.impl.visual.ClickUI;
import cn.yapeteam.yolbi.module.impl.player.AutoArmor;
import cn.yapeteam.yolbi.module.impl.visual.*;
import cn.yapeteam.yolbi.module.impl.world.FastPlace;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.notification.Notification;
import cn.yapeteam.yolbi.notification.NotificationType;
import cn.yapeteam.yolbi.utils.animation.Easing;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Getter
@SuppressWarnings({"unchecked", "unused"})
public class ModuleManager {
    private final List<Module> modules = new CopyOnWriteArrayList<>();
   // private final BooleanValue notif = ClickUI.notification.getValue();
    private final BooleanValue notiffff = ClientTheme.notifi;
    public void load() {
        modules.add(new AimAssist());
        modules.add(new AntiBot());
        modules.add(new AutoClicker());
        // modules.add(new Backtrack());
        //modules.add(new IRC()); // To be tested
        // modules.add(new BlatantVelocity());
        // modules.add(new Criticals());
        // modules.add(new FakeLag());
        modules.add(new KillAura());
        // modules.add(new Reach());
        modules.add(new CombatSettings());
        modules.add(new Velocity());
        modules.add(new WTap());
        modules.add(new AntiInvisible());
        modules.add(new AutoArmor());
        modules.add(new ChestStealer());
        modules.add(new ClientSpoof());
        modules.add(new NoteBot());
        modules.add(new SelfDestruct());
        modules.add(new Eagle());
        modules.add(new NoSlow());
        modules.add(new MoveFix());
        //modules.add(new Scaffold());
        modules.add(new Sprint());
        modules.add(new StrafeFix());
        modules.add(new ClickUI());
        modules.add(new ClientTheme());
        modules.add(new ESP());
        modules.add(new HeadUpDisplay());
        modules.add(new JFrameESP2D());
        modules.add(new JFrameRenderer());
        modules.add(new PacketDebug());
        modules.add(new TargetHud());
        modules.add(new MurdererFinder());
        modules.add(new NameTags());
        modules.add(new FastPlace());


        modules.sort((m1, m2) -> -Integer.compare(m2.getName().charAt(0), m1.getName().charAt(0)));
    }

    @Listener
    private void onKey(EventKey e) {
        modules.stream().filter(m -> m.getKey() == e.getKey()).collect(Collectors.toList()).forEach(module -> {
            module.toggle();
            if(notiffff.getValue()){ // Notification -> ClientTheme
                    YolBi.instance.getNotificationManager().post(new Notification(
                        module.getName() + (module.isEnabled() ? " Enabled" : " Disabled"),

                        Easing.EASE_OUT_BACK, Easing.EASE_IN_OUT_CUBIC,
                        1500, module.isEnabled() ? NotificationType.SUCCESS : NotificationType.FAILED
                        ));
            }


        });
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        return (T) modules.stream().filter(m -> m.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public <T extends Module> T getModuleByName(String name) {
        return (T) modules.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Module> getModulesByCategory(ModuleCategory category) {
        return modules.stream().filter(m -> m.getCategory() == category).collect(Collectors.toList());
    }
}