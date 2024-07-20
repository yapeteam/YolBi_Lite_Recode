(function () {
    class DraggablePanel {
        constructor(elementId) {
            this.element = document.getElementById(elementId);
            this.element.style.position = 'absolute';
            this.element.addEventListener('mousedown', this.handleDragStart.bind(this));
            document.addEventListener('mousemove', this.handleDrag.bind(this));
            document.addEventListener('mouseup', this.handleDragEnd.bind(this));
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
            event.preventDefault();
            this.setMaxZIndex();
            const style = this.element.style;
            this.startX = event.clientX - this.convert(style.left);
            this.startY = event.clientY - this.convert(style.top);
            this.dragging = true;
        }

        handleDrag(event) {
            if (!this.dragging) return;
            const style = this.element.style;
            style.left = `${event.clientX - this.startX}px`;
            style.top = `${event.clientY - this.startY}px`;
        }

        handleDragEnd(_) {
            this.dragging = false;
        }
    }

    document.addEventListener('DOMContentLoaded', () => {
        new DraggablePanel('ele1');
        new DraggablePanel('ele2');
    });
})();