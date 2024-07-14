package cn.yapeteam.yolbi.module;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventKey;
import cn.yapeteam.yolbi.module.impl.misc.SelfDestruct;
import cn.yapeteam.yolbi.module.impl.visual.ClientTheme;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Getter
@SuppressWarnings({"unchecked", "unused"})
public class ModuleManager {
    private final List<Module> modules = new CopyOnWriteArrayList<>();

    public void load() {
        modules.add(new SelfDestruct());
        modules.add(new ClientTheme());

        modules.sort((m1, m2) -> -Integer.compare(m2.getName().charAt(0), m1.getName().charAt(0)));
    }

    @Listener
    private void onKey(EventKey e) {
        modules.stream().filter(m -> m.getKey() == e.getKey()).collect(Collectors.toList()).forEach(Module::toggle);
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
