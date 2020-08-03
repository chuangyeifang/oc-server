    /**
     * 获得base64
     * @param {Object} obj
     * @param {Number} [obj.width] 图片需要压缩的宽度，高度会跟随调整
     * @param {Number} [obj.quality=0.8] 压缩质量，不压缩为1
     * @param {Function} [obj.before(this, blob, file)] 处理前函数,this指向的是input:file
     * @param {Function} obj.success(obj) 处理后函数
     * @example
     *
     */
    $.fn.localResizeIMG = function(obj) {
        this.on('change', function() {
            var file = this.files[0];
            var URL = window.URL || window.webkitURL;
            var blob = URL.createObjectURL(file);

            if ((file.type).indexOf("image/") == -1) {
                alert("请上传图片");
                return;
            }
            // 执行前函数
            if ($.isFunction(obj.before)) {
                obj.before(this, blob, file)
            };

            _create(blob, file);
            //原代码上是清空掉数据,如果结合MultipartFile不需要清空,否则MultipartFile 对象报空指针异常, Edit By z.k.f
            //this.value = ''; // 清空临时数据
        });

        /**
         * 生成base64
         * @param blob 通过file获得的二进制
         */
        function _create(blob) {
            var img = new Image();
            img.src = blob;

            img.onload = function() {
                try {
                    var that = this;

                    //生成比例
                    var w = that.width,
                        h = that.height,
                        scale = w / h;
                    if (w && obj && obj.width && obj.width < w) {
                        w = obj.width;
                        h = w / scale;
                    }
                    //生成canvas
                    var canvas = document.createElement('canvas');
                    var ctx = canvas.getContext('2d');
                    $(canvas).attr({
                        width: w,
                        height: h
                    });
                    ctx.drawImage(that, 0, 0, w, h);

                    /**
                     * 生成base64
                     * 兼容修复移动设备需要引入mobileBUGFix.js
                     */
                    var base64 = canvas.toDataURL('image/jpeg', obj.quality || 0.8);

                    // 修复IOS
                    if (navigator.userAgent.match(/iphone/i)) {
                        var mpImg = new MegaPixImage(img);
                        mpImg.render(canvas, {
                            maxWidth: w,
                            maxHeight: h,
                            quality: obj.quality || 0.8
                        });
                        base64 = canvas.toDataURL('image/jpeg', obj.quality || 0.8);
                    }

                    // 修复android
                    if (navigator.userAgent.match(/Android/i)) {
                        var encoder = new JPEGEncoder();
                        base64 = encoder.encode(ctx.getImageData(0, 0, w, h), obj.quality * 100 || 80);
                    }

                    // 生成结果
                    var result = {
                        ret: 0,
                        base64: base64,
                        clearBase64: base64.substr(base64.indexOf(',') + 1)
                    };

                    // 执行后函数
                    obj.success(result);
                } catch (e) {
                    var result = {
                        ret: 1,
                        msg: e
                    };
                    obj.success(result);
                }
            };
        }
    };