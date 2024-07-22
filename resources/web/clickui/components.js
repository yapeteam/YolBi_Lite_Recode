(function () {
  class Panel {
    constructor(element) {
      this.element = element;
      this.element.style.position = "absolute";
      this.element.addEventListener(
        "mousedown",
        this.handleDragStart.bind(this)
      );
      document.addEventListener("mousemove", this.handleDrag.bind(this));
      document.addEventListener("mouseup", this.handleDragEnd.bind(this));
    }

    convert(v) {
      return v.substring(0, v.length - 2);
    }

    setMaxZIndex() {
      let maxZIndex = 0;
      const elements = document.querySelectorAll("*");
      elements.forEach(function (el) {
        const zIndex = parseInt(window.getComputedStyle(el).zIndex, 10);
        if (zIndex > maxZIndex) maxZIndex = zIndex;
      });
      this.element.style.zIndex = String(maxZIndex + 1);
    }

    handleDragStart(event) {
      event.preventDefault();
      const style = this.element.style;
      this.startX = event.clientX - this.convert(style.left);
      this.startY = event.clientY - this.convert(style.top);
      this.setMaxZIndex();
      this.dragging = this.startY <= 35;
    }

    handleDrag(event) {
      event.preventDefault();
      if (!this.dragging) return;
      const style = this.element.style;
      style.left = `${event.clientX - this.startX}px`;
      style.top = `${event.clientY - this.startY}px`;
    }

    handleDragEnd(_) {
      this.dragging = false;
    }
  }

  class Module {
    constructor(element, parentCat, index) {
      this.element = element;
      this.parent = parentCat;
      this.index = index;
      this.enabled = false;
      this.key = 0;
    }

    convert(v) {
      return v.substring(0, v.length - 2);
    }
  }

  class Value {
    constructor(element, parentMod, index) {
      this.element = element;
      this.parent = parentMod;
      this.index = index;
      this.enabled = false;
      this.key = 0;
    }

    convert(v) {
      return v.substring(0, v.length - 2);
    }
  }

  document.addEventListener("DOMContentLoaded", () => {
    window.mcefQuery({
      request: "clickui/cats: ",
      persistent: false,
      onSuccess: function (msg) {
        const array = JSON.parse(msg);
        for (let i = 0; i < array.length; i++) {
          const val = array[i];
          const panelDiv = document.createElement("div");
          panelDiv.id = val;
          panelDiv.classList.add("panel");
          panelDiv.classList.add("content");
          panelDiv.innerHTML = `<a>${val}</a>`;
          panelDiv.style.zIndex = String(i);
          panelDiv.style.left = 20 + (50 + 200) * i + "px";
          panelDiv.style.top = "80px";
          document.body.appendChild(panelDiv);
          new Panel(panelDiv);
          window.mcefQuery({
            request: `clickui/mods:${val}`,
            persistent: false,
            onSuccess: function (msg) {
              const array = JSON.parse(msg);
              for (let i = 0; i < array.length; i++) {
                const val = array[i];
                const moduleDiv = document.createElement("div");
                moduleDiv.classList.add("module");
                moduleDiv.classList.add("content");
                const nameDiv = document.createElement("div");
                nameDiv.innerHTML = `${val}`;
                nameDiv.classList.add("moduleName");
                moduleDiv.appendChild(nameDiv);
                panelDiv.appendChild(moduleDiv);
                const module = new Module(moduleDiv, panelDiv, i);
                window.mcefQuery({
                  request: `clickui/values:${val}`,
                  onSuccess: function (msg) {
                    const array = JSON.parse(msg);

                    for (let i = 0; i < array.length; i++) {
                      const obj = array[i];
                      const entries = Object.entries(obj);
                      for (let j = 0; j < entries.length; j++) {
                        const entry = entries[j];
                        const key = entry[0];
                        const value = entry[1];
                        switch (key) {
                          case "bool:enabled":
                            module.enabled = Boolean(value);
                            break;
                          case "number:key":
                            module.key = Number(value);
                            break;
                          default:
                            switch (key) {
                              case "min":
                                break;
                              case "max":
                                break;
                              case "inc":
                                break;
                              case "modes":
                                break;
                              default:
                                const type_name = key.split(":");
                                const type = type_name[0];
                                const name = type_name[1];
                                const valueDiv = document.createElement("div");
                                valueDiv.classList.add("value");
                                valueDiv.innerHTML = `${name}: ${value}`;
                                console.log(key);
                                moduleDiv.appendChild(valueDiv);
                                new Value(valueDiv, moduleDiv, j);
                            }
                        }
                      }
                    }
                  },
                  onFailure: function (err, msg) {
                    console.log(msg);
                  },
                });
              }
            },
            onFailure: function (err, msg) {
              console.log(msg);
            },
          });
        }
      },
      onFailure: function (err, msg) {
        console.log(msg);
      },
    });
  });
})();
