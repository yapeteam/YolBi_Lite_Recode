class Panel {
    constructor(elementId) {
        this.element = document.getElementById(elementId);
        this.element.draggable = true;
        this.element.style.position = 'absolute';
        this.element.addEventListener('dragstart', this.handleDragStart.bind(this));
        this.element.addEventListener('drag', this.handleDrag.bind(this));
    }

    convert(v) {
        return v.substring(0, v.length - 2);
    }

    setMaxZIndex() {
        let maxZIndex = 0;
        const elements = document.querySelectorAll('*');
        elements.forEach(function (el) {
            const zIndex = parseInt(window.getComputedStyle(el).zIndex, 10);
            if (zIndex > maxZIndex)
                maxZIndex = zIndex;
        });
        this.element.style.zIndex = String(maxZIndex + 1);
    }

    handleDragStart(event) {
        this.setMaxZIndex();
        const style = this.element.style;
        this.startX = event.clientX - this.convert(style.left);
        this.startY = event.clientY - this.convert(style.top);
        const img = new Image();
        img.src = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7';
        event.dataTransfer.setDragImage(img, 0, 0);
    }

    handleDrag(event) {
        const style = this.element.style;
        style.left = `${event.clientX - this.startX}px`;
        style.top = `${event.clientY - this.startY}px`;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new Panel('ele1');
    new Panel('ele2');
});