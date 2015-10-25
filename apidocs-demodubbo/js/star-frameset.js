/*
    Usage:
        <div class='frameset' rows="30%,70%">
            <div><p>hi</p></div>
            <div><p>hi</p></div>
            <!-- <div class='frameset' cols="50%,*">
                <div id="d21"><p>hi</p></div>
                <div id="d22"><p>hi</p></div>
            </div> -->
        </div>
*/

;
(function($) {

    var rowLineCss = 'position:absolute;overflow:hidden;height:4px;border-top:1px solid #d7d7d7;border-bottom:1px solid #d7d7d7;background:#f8f8f8;cursor:n-resize;z-index: 9999',
        colLineCss = 'position:absolute;overflow:hidden;width:4px;border-left:1px solid #d7d7d7;border-right:1px solid #d7d7d7;background:#f8f8f8;cursor:e-resize;z-index: 9999';
    var margin = 3;

    function disableSelection(cursor) {
        document.onselectstart = function() {
            return false;
        }
        $('body').css({
            '-o-user-select': 'none',
            '-moz-user-select': 'none',
            'cursor': cursor
        });
    }

    function enableSelection() {
        document.onselectstart = null;
        $('body').css({
            '-o-user-select': '',
            '-moz-user-select': '',
            'cursor': ''
        });
    }

    // handle multiple browsers for requestAnimationFrame()
    window.requestAnimationFrame = (function() {
        return window.requestAnimationFrame ||
            window.webkitRequestAnimationFrame ||
            window.mozRequestAnimationFrame ||
            window.oRequestAnimationFrame ||
            // if all else fails, use setTimeout
            function(callback) {
                return window.setTimeout(callback, 1000 / 60); // shoot for 60 fps
            };
    })();

    // handle multiple browsers for cancelAnimationFrame()
    window.cancelAnimationFrame = (function() {
        return window.cancelAnimationFrame ||
            window.webkitCancelAnimationFrame ||
            window.mozCancelAnimationFrame ||
            window.oCancelAnimationFrame ||
            function(id) {
                window.clearTimeout(id);
            };
    })();

    function getFrameExtent(framesetExtent, rowsOrCols) {
        var rs = $.trim(rowsOrCols).split(/\s*,\s*/);
        var res = [];
        var allIndex;
        for (var i = 0, len = rs.length; i < len; i++) {
            if (/%$/.test(rs[i])) {
                res[i] = parseFloat(rs[i]) / 100 * framesetExtent;
            } else if (/^[\d\.]+$/.test(rs[i])) {
                res[i] = parseFloat(rs[i]);
                if (i !== 0) {
                    res[i] += margin;
                }
            } else if (/^\*$/.test(rs[i])) {
                res[i] = 0;
                allIndex = i;
            }
        }
        if (typeof allIndex === 'number') {
            res[allIndex] = framesetExtent - plusAll(res);
        }
        return res;
    }

    function plusAll(arr) {
        var res = 0;
        for (var i = 0, len = arr.length; i < len; i++) {
            res += parseFloat(arr[i]);
        }
        return res;
    }

    function StarFrameset(dom) {
        this.frameset = $(dom);
        this.frames = this.frameset.children();
        this.init();
    }

    StarFrameset.prototype = {
        init: function() {
            var framesetHeight = this.frameset.innerHeight(),
                framesetWidth = this.frameset.innerWidth(),
                rows = this.frameset.attr('rows'),
                cols = this.frameset.attr('cols'),
                frame;
            if (rows && cols) {
                throw ("can't set the cols and rows at one time.");
            }
            var positionCss = this.frameset.css('position');
            if (positionCss !== 'absolute' && positionCss !== 'relative') {
                this.frameset.css('position', 'relative');
            }

            if (rows) {
                var rowsExtent = getFrameExtent(framesetHeight, rows);
                for (var i = 0, len = rowsExtent.length; i < len; i++) {
                    frame = this.frames.eq(i);
                    var height = rowsExtent[i];
                    var css = {
                        'height': height + 'px',
                        'overflow': 'auto',
                        'box-sizing': 'border-box'
                    };

                    if (frame.hasClass('frameset')) {
                        css['overflow'] = 'hidden';
                    }
                    if (i !== 0) {
                        css['margin-top'] = margin + 'px';
                        css['height'] = (height - margin) + 'px';
                    }
                    frame.css(css);
                    if (i < len - 1) {
                        if (!this['row' + i]) {
                            var rowLine = this.addRowLine(frame);
                            this['row' + i] = rowLine;
                        }
                        this.renderRowLine(this['row' + i]);
                    }
                }
            } else if (cols) {
                var colsExtent = getFrameExtent(framesetWidth, cols);
                for (var i = 0, len = colsExtent.length; i < len; i++) {
                    frame = this.frames.eq(i);
                    var width = colsExtent[i];
                    var css = {
                        'height': '100%',
                        'width': width + 'px',
                        'float': 'left',
                        'overflow': 'auto',
                        'box-sizing': 'border-box'
                    };
                    if (frame.hasClass('frameset')) {
                        css['overflow'] = 'hidden';
                    }
                    if (i !== 0) {
                        css['margin-left'] = margin + 'px';
                        css['width'] = (width - margin) + 'px';
                    }
                    frame.css(css);
                    if (i < len - 1) {
                        if (!this['col' + i]) {
                            var colLine = this.addColLine(frame);
                            this['col' + i] = colLine;
                        }
                        this.renderColLine(this['col' + i]);
                    }
                }
            }

        },

        on: function(line) {
            line.on('mousedown', function(e) {
                target = line;
                var frame = target.frame;
                xy.fx = frame.outerWidth();
                xy.fy = frame.outerHeight();
                xy.nx = frame.next().outerWidth();
                xy.ny = frame.next().outerHeight();
                xy.lx = parseFloat(target.css('left')),
                    xy.ly = parseFloat(target.css('top')),
                    xy.x = e.clientX;
                xy.y = e.clientY;
                if (line.direction === 'y') {
                    disableSelection('n-resize');
                } else {
                    disableSelection('e-resize');
                }
            });
        },

        addRowLine: function(frame) {
            var line = $('<div style="' + rowLineCss + '"></div>');
            line.direction = 'y';
            line.frame = frame;
            frame.minHeight = parseFloat(frame.css('min-height')) || 30;
            this.frameset.append(line);
            this.on(line);
            return line;
        },

        renderRowLine: function(line) {
            var frame = line.frame;
            var left = frame.get(0).offsetLeft,
                top = frame.get(0).offsetTop;
            line.css({
                'left': left + 'px',
                'top': (top + frame.outerHeight() - 3) + 'px',
                'width': '100%'
            });
        },

        addColLine: function(frame) {
            var line = $('<div style="' + colLineCss + '"></div>');
            line.direction = 'x';
            line.frame = frame;
            this.frameset.append(line);
            this.on(line);
            return line;
        },

        renderColLine: function(line) {
            var frame = line.frame;
            var left = frame.get(0).offsetLeft,
                top = frame.get(0).offsetTop;
            line.css({
                'left': (left + frame.outerWidth() - 3) + 'px',
                'top': top + 'px',
                'height': '100%'
            });
        }
    }

    var target,
        originX,
        originY;
    var xy = {
        x: 0,
        y: 0,
        fx: 0,
        fy: 0,
        nx: 0,
        ny: 0,
        lx: 0,
        ly: 0
    };
    var requestId,
        framesetList = [];

    $(document).on('mousemove', function(e) {
        var scroll = 30;
        if (!target) {
            return;
        }
        
        if (target.direction === 'y') {
            var y = e.clientY - xy.y;
            if (xy.fy + y < Math.max(target.frame.minHeight, scroll) || xy.ny - y < scroll) {
                return;
            }
            requestId = window.requestAnimationFrame(function() {

                target.frame.css({
                    'height': (xy.fy + y) + 'px'
                });
                target.frame.next().css({
                    'height': (xy.ny - y) + 'px'
                });
                target.css({
                    'top': (xy.ly + y) + 'px'
                });
            });
        } else if (target.direction === 'x') {
            var x = e.clientX - xy.x;
            if (xy.fx + x < scroll || xy.nx - x < scroll) {
                return;
            }
            requestId = window.requestAnimationFrame(function() {

                target.frame.css({
                    'width': (xy.fx + x) + 'px'
                });
                target.frame.next().css({
                    'width': (xy.nx - x) + 'px'
                });
                target.css({
                    'left': (xy.lx + x) + 'px'
                });
            });
        }
    }).on('mouseup', function(e) {
        target = null;
        if (requestId) {
            window.cancelAnimationFrame(requestId);
        }
        enableSelection();
    });

    var resizeTimer;
    $(window).on('resize', function(e) {
        resizeTimer && clearTimeout(resizeTimer);
        resizeTimer = setTimeout(function() {
            for (var i = 0, len = framesetList.length; i < len; i++) {
                framesetList[i].init();
            }
        }, 70);
    });    

    $.fn.starFrameset = function() {
        this.each(function() {
            var data = $(this).data('star-frameset');
            if (!data) {
                var starFrameset = new StarFrameset(this);
                framesetList.push(starFrameset)
                $(this).data('star-frameset', starFrameset);
            }
        });
        return this;
    }

    $('.frameset').starFrameset();


})(jQuery);