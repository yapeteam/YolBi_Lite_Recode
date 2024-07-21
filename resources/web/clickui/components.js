(function () {
  class Panel {
    constructor(elementId) {
      this.element = document.getElementById(elementId);
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
      this.element.style.position = "absolute";
      this.element.style.top = `${40 + 46 * this.index}px`;

      document.addEventListener("mousemove", this.handleDrag.bind(this));
    }

    convert(v) {
      return v.substring(0, v.length - 2);
    }

    handleDrag(event) {
      event.preventDefault();
      if (!this.parent.dragging) return;
      const style = this.element.style;
      style.left = this.parent.style.left;
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
          const newDiv = document.createElement("div");
          newDiv.id = val;
          newDiv.classList.add("panel");
          newDiv.classList.add("content");
          newDiv.innerHTML = `<a>${val}</a>`;
          newDiv.style.zIndex = String(i);
          newDiv.style.left = 20 + (50 + 200) * i + "px";
          newDiv.style.top = "80px";
          document.body.appendChild(newDiv);
          new Panel(val);
          window.mcefQuery({
            request: `clickui/mods:${val}`,
            persistent: false,
            onSuccess: function (msg) {
              const array = JSON.parse(msg);
              for (let i = 0; i < array.length; i++) {
                const val = array[i];
                const moduleDiv = document.createElement("div");
                moduleDiv.classList.add("module");
                const nameDiv = document.createElement("div");
                nameDiv.innerHTML = `${val}`;
                nameDiv.classList.add("moduleName");
                moduleDiv.appendChild(nameDiv);
                newDiv.appendChild(moduleDiv);
                new Module(moduleDiv, newDiv, i);
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
