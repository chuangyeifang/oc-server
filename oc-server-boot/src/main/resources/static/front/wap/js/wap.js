function _typeof(obj) { "@babel/helpers - typeof"; if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

var Pdialog = {
  showDialog: function showDialog(options) {
    function doShowDialog() {
      options = $.extend({
        title: "提示",
        content: "提示框"
      }, options);
      var pDialogMaskWrapper = '<div id="p_dialog_mask_wrapper">';
      var pImgContainer = '<div id="p_dialog_container">';
      var pDialogHeader = '<div id="p_dialog_header"><div id="p_dialog_header_name">' + options.title + '</div><div id="p_dialog_close"></div></div>';
      var pDialogContent = '<div id="p_dialog_content">' + options.content + '</div>';
      var pImgOpts = '<div id="p_dialog_opts">' + '<span id="p_dialog_submit">关闭</span>' + '</div>';
      var endTag = "</div>";
      $('#p_dialog_mask_wrapper').remove();
      var appendHtml = pDialogMaskWrapper + pImgContainer + pDialogHeader + pDialogContent + pImgOpts + endTag;
      $('body').append(appendHtml);
      initEvent();
    }

    function initEvent() {
      $('#p_dialog_mask_wrapper').on('click', function () {
        $('#p_dialog_mask_wrapper').remove();
      });
      $('#p_dialog_close').on('click', function () {
        $('#p_dialog_mask_wrapper').remove();
      });
      $('#p_dialog_container').on('click', function (event) {
        Common.stopPropagation(event);
      });
      $('#p_dialog_submit').on('click', function () {
        $('#p_dialog_mask_wrapper').remove();
      });
    }

    doShowDialog();
  }
};
/*
 * 替换html标签
 * */

var EscapeSequence = {
  // 正则去掉非法标签<html> <javascript> 多余换行及空格, 除<br>
  filterHtmlJsSpaceEnter: function filterHtmlJsSpaceEnter(str) {
    if (str && str.length > 0) {
      str = str.replace(/[\s| | ]*\r/gi, ' ');
      str = str.replace(/<(?!br([/]*)>)[^>]*>/gi, "");
    }

    return str;
  },
  // 用正则表达式实现html转码
  htmlEncodeByRegExp: function htmlEncodeByRegExp(str) {
    var s = "";
    if (str.length == 0) return "";
    s = str.replace(/&/g, "&amp;");
    s = s.replace(/</g, "&lt;");
    s = s.replace(/>/g, "&gt;");
    s = s.replace(/ /g, " ");
    s = s.replace(/\'/g, "&#39;");
    s = s.replace(/\"/g, "&quot;");
    return s;
  },
  // 用正则表达式实现html解码
  htmlDecodeByRegExp: function htmlDecodeByRegExp(str) {
    var s = "";
    if (str.length == 0) return "";
    s = str.replace(/&amp;/g, "&");
    s = s.replace(/&lt;/g, "<");
    s = s.replace(/&gt;/g, ">");
    s = s.replace(/&nbsp;/g, " ");
    s = s.replace(/&#39;/g, "\'");
    s = s.replace(/&quot;/g, "\"");
    return s;
  },
  //html转字符
  htmlTurnCharacter: function htmlTurnCharacter(str) {
    var s = "";
    if (str.length == 0) return "";
    var regex = /(.*?)<img.*?alt="(.*?)">/ig;
    s = str.replace(regex, "$1$2");
    return s;
  }
};

function edittable() {
  $('[contenteditable]').each(function () {
    try {
      document.execCommand("AutoUrlDetect", false, false);
    } catch (e) {}

    $(this).on('paste', function (e) {
      e.preventDefault();
      var text = null;

      if (window.clipboardData && clipboardData.setData) {
        text = window.clipboardData.getData('text');
      } else {
        text = (e.originalEvent || e).clipboardData.getData('text/plain');
      }

      if (!text) {
        return false;
      }

      if (document.body.createTextRange) {
        console.log('insert Text');

        if (document.selection) {
          textRange = document.selection.createRange();
        } else if (window.getSelection) {
          sel = window.getSelection();
          var range = sel.getRangeAt(0);
          var tempEl = document.createElement("span");
          tempEl.innerHTML = "&#FEFF;";
          range.deleteContents();
          range.insertNode(tempEl);
          textRange = document.body.createTextRange();
          textRange.moveToElementText(tempEl);
          tempEl.parentNode.removeChild(tempEl);
        }

        textRange.text = text;
        textRange.collapse(false);
        textRange.select();
      } else {
        // Chrome之类浏览器
        document.execCommand("insertText", false, text);
      }
    }); // 去除Crtl+b/Ctrl+i/Ctrl+u等快捷键

    $(this).on('keydown', function (e) {
      // e.metaKey for mac
      if (e.ctrlKey || e.metaKey) {
        switch (e.keyCode) {
          case 66: //ctrl+B or ctrl+b

          case 98:
          case 73: //ctrl+I or ctrl+i

          case 105:
          case 85: //ctrl+U or ctrl+u

          case 117:
            e.preventDefault();
            break;
        }
      }
    });
  });
}

var Config = {
  domain: 'jshii.com.cn',
  ws: 'ws://im.jshii.com.cn:6066',
  poll: 'http://im.jshii.com.cn:6066',
  api: 'http://im.jshii.com.cn:8080'
};
var base = Config.api;
var Api = {
  auth: base + "/customer/auth",
  opinion: base + "/opinion/rate",
  chatRecord: base + "/chat/before",
  hot: base + "/hot/question",
  uploadImage: base + "/upload/image",
  uploadImgb64: base + "/upload/imgb64",
  goodsDetail: base + "/goods/info",
  recommendList: base + "/recommend/list",
  orderList: base + "/order/list",
  logistics: base + "/order/logistics"
};
var Request = {
  get: function get(url, params, async, _success, _error) {
    params.t = new Date().getTime();
    $.ajax({
      type: "GET",
      url: url,
      data: params,
      dataType: 'json',
      async: async,
      xhrFields: {
        withCredentials: true
      },
      success: function success(res) {
        _success && _success instanceof Function && _success(res);
      },
      error: function error(res) {
        _error && _error instanceof Function && _error(res);
      }
    });
  },
  post: function post(url, params, async, _success2, _error2) {
    params.t = new Date().getTime();
    $.ajax({
      type: "POST",
      url: url,
      data: params,
      dataType: 'json',
      async: async,
      xhrFields: {
        withCredentials: true
      },
      success: function success(res) {
        _success2 && _success2 instanceof Function && _success2(res);
      },
      error: function error(res) {
        _error2 && _error2 instanceof Function && _error2(res);
      }
    });
  },
  jsonp: function jsonp(url, params, async, _success3, _error3) {
    params.t = new Date().getTime();
    $.ajax({
      type: "POST",
      url: url,
      data: params,
      dataType: 'jsonp',
      async: async,
      jsonp: "callback",
      xhrFields: {
        withCredentials: true
      },
      success: function success(res) {
        _success3 && _success3 instanceof Function && _success3(res);
      },
      error: function error(res) {
        _error3 && _error3 instanceof Function && _error3(res);
      }
    });
  },
  uploadImg: function uploadImg(url, formdata, async, _success4, _error4) {
    $.ajax({
      url: url,
      data: formdata,
      dataType: 'json',
      type: "POST",
      async: async,
      processData: false,
      contentType: false,
      xhrFields: {
        withCredentials: true
      },
      // 添加cookie标识
      success: function success(res) {
        _success4 && _success4 instanceof Function && _success4();
      },
      error: function error(res) {
        _error4 && _error4 instanceof Function && _error4();
      }
    });
  }
};
var Constant = {
  build_success: "会话创建成功！",
  waitting_tip: "当前客服繁忙，已加入等候队列（您前面还有 {0} 个人）请您耐心等候...",
  service_info: "客服工号 {0} 为您提供服务~",
  close_tip: "会话已结束（回复再次咨询）."
};
var subId = 1000;
var Common = {
  getCookie: function getCookie(name) {
    var arr;
    var reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");

    if (arr = document.cookie.match(reg)) {
      return unescape(arr[2]);
    } else {
      return "oc-" + ((1 + Math.random()) * 0x10000 | 0).toString(16).substring(1);
    }
  },
  getPid: function getPid() {
    return subId++ + "-" + fragment() + '-' + fragment() + '-' + fragment() + '-' + fragment() + '-' + fragment() + fragment() + fragment();
  },
  dateFormat: function dateFormat(obj) {
    var date;

    if (obj && obj instanceof Date) {
      date = obj;
    } else {
      date = new Date();
    }

    var y = date.getFullYear(),
        m = date.getMonth() + 1,
        d = date.getDate();
    return y + "-" + (m < 10 ? "0" + m : m) + "-" + (d < 10 ? "0" + d : d) + " " + date.toTimeString().substr(0, 8);
  },
  getUrlParams: function getUrlParams() {
    var urlSearch = decodeURI(location.search);

    if (urlSearch.indexOf("?") == 1) {
      return null;
    }

    urlSearch = urlSearch.substr(1);
    var urls = urlSearch.split("&");
    var params = {};
    var temp;

    for (var i = 0; i < urls.length; i++) {
      temp = urls[i].split("=");
      params[temp[0]] = temp[1];
    }

    return params;
  },
  isMobile: function isMobile() {
    var agent = navigator.userAgent;
    return !!agent.match(/AppleWebKit.*Mobile.*/) || !!agent.match(/AppleWebKit/) && agent.indexOf('QIHU') && agent.indexOf('QIHU') > -1 && agent.indexOf('Chrome') < 0;
  },
  uploadImg: function uploadImg(url, callback) {
    var fileForm = '<form id="submit_upload_img" method="post" action="' + url + '" enctype="multipart/form-data" target="iframe_upload_img" style="display: none">' + '<input type="file" id="input_upload_img" name="file" accept=".jpg,.gif,.jpeg,.png" /></form>';
    var iframe = '<iframe id="iframe_upload_img" name="iframe_upload_img" style="display: none"></iframe>';
    $('#iframe_upload_img').remove();
    $('#submit_upload_img').remove();
    $('body').append(iframe);
    $('body').append(fileForm);
    $('#input_upload_img').change(function () {
      $("#submit_upload_img").submit();
    });
    $("#iframe_upload_img").load(function (e) {
      var res = $("#iframe_upload_img")[0].contentWindow.document.querySelector('body').innerHTML;
      res = JSON.parse(res);
      callback && callback instanceof Function && callback(res);
    });
    $("#input_upload_img").click();
  },
  uploadImgB64: function uploadImgB64(url, imgB64, callback) {
    var fileForm = '<form id="submit_upload_img" method="post" action="' + url + '" enctype="multipart/form-data" target="iframe_upload_img" style="display: none">' + '<input id="input_upload_img" name="imgb64" value="' + imgB64 + '" accept=".jpg,.gif,.jpeg,.png" /></form>';
    var iframe = '<iframe id="iframe_upload_img" name="iframe_upload_img" style="display: none"></iframe>';
    $('#iframe_upload_img').remove();
    $('#submit_upload_img').remove();
    $('body').append(iframe);
    $('body').append(fileForm);
    $("#iframe_upload_img").load(function (e) {
      var res = $("#iframe_upload_img")[0].contentWindow.document.querySelector('body').innerHTML;
      res = JSON.parse(res);
      callback && callback instanceof Function && callback(res);
    });
    $("#submit_upload_img").submit();
  },
  createImgHtml: function createImgHtml(imgurl) {
    return '<img src="' + imgurl + '" style="max-width: 100%; max-height: 300px;" onload="conFitScroll()" />';
  },
  stopPropagation: function stopPropagation(event) {
    if (window.showModalDialog) {
      window.event.cancelBubble = true;
    } else {
      event.stopPropagation();
    }
  }
};

function fragment() {
  return Math.floor(65535 * (1 + Math.random())).toString(16).substring(1);
}

(function (root, factory) {
  (typeof exports === "undefined" ? "undefined" : _typeof(exports)) === 'object' && typeof module !== 'undefined' ? module.exports = factory() : typeof define === 'function' && define.amd ? define(factory) : root.iNotify = factory();
})(window, function () {
  var oldTitle = document.title;

  var iNotify = function iNotify(opts) {
    this.init(opts || {});
  };

  iNotify.prototype.init = function (opts) {
    this.open = false;
    this.effect = opts.effect || "flash";
    this.message = opts.message || "有新消息了!";
    this.title = document.title || this.message;
    this.interval = opts.interval || 500;
    this.onceTime = opts.onceTime || 2000;
    this.isActiveW = false; //当前窗口是不是激活状态
    //开始检测状态

    this.onMonitor();
  };

  iNotify.prototype.start = function () {
    var _this = this;

    _this.stop(); //falsh 消息闪动title


    if (_this.effect === "flash") {
      var isOld = false;
      return _this.flashInterval = setInterval(function () {
        isOld ? document.title = oldTitle : document.title = _this.message;
        isOld = !isOld;
      }, _this.interval);
    } //scroll 消息滚动title


    if (_this.effect === "scroll") {
      document.title = _this.message;
      return _this.scrollInterval = setInterval(function () {
        var text = document.title;
        document.title = text.substring(1, text.length) + text.substring(0, 1);
        text = document.title.substring(0, text.length);
      }, _this.interval);
    }
  };

  iNotify.prototype.stop = function () {
    var _this = this;

    _this.isActiveW = false;
    clearTimeout(_this.onceTimeout);

    if (_this.effect === "flash") {
      _this.flashInterval && clearInterval(_this.flashInterval);
      return document.title = oldTitle;
    }

    if (_this.effect === "scroll") {
      _this.scrollInterval && clearInterval(_this.scrollInterval);
      return document.title = oldTitle;
    }
  };

  iNotify.prototype.once = function () {
    var _this = this;

    _this.stop();

    if (_this.effect === "flash" || _this.effect === "scroll") {
      clearTimeout(_this.onceTimeout);

      _this.start();

      return _this.onceTimeout = setTimeout(function () {
        _this.stop();
      }, _this.onceTime);
    }
  };

  iNotify.prototype.onMonitor = function () {
    var _this = this;

    window.onfocus = function () {
      if (!_this.isActiveW) {
        _this.isActiveW = true;

        _this.stop();
      }
    };

    document.onclick = function () {
      if (!_this.isActiveW) {
        _this.isActiveW = true;

        _this.stop();
      }
    };
  };

  return iNotify;
});

var notify = new iNotify({
  effect: "scroll",
  message: "您有新消息..."
});
var ChatResolver = {
  type: {
    inMessage: "inMessage",
    outMessage: "outMessage",
    broadcast: "broadcast"
  },
  render: function render(type, message) {
    switch (type) {
      case this.type.inMessage:
        renderInMessage(message);
        break;

      case this.type.outMessage:
        renderOutMessage(message);
        break;

      case this.type.broadcast:
        renderBroadcast(message);
        break;
    }
  },
  renderHistory: function renderHistory(message) {
    renderHistoryRecord(message);
  },
  renderBroadcast: function renderBroadcast(message) {
    $("#chat_service_status_msg").text(message);
  },
  renderHtml: function renderHtml(html) {
    appendHtml(html);
  },
  toScroll: function toScroll() {
    fitScroll();
  }
};
var revocationMessage = "客服撤回一条消息";

function renderInMessage(message) {
  var messageType = message.type;

  switch (messageType) {
    case "BUILD_CHAT":
      customer.cid = message.cid;
      renderInMessage.buildChat(message);
      break;

    case "MESSAGE":
      renderInMessage.message(message);
      break;

    case "TRANSFER":
      renderInMessage.transfer(message);
      break;

    case "BROADCAST":
      renderInMessage.broadcast(message);
      break;

    case "CLOSE_CHAT":
      renderBroadcast(Constant.close_tip);
      break;

    case "REVOCATION":
      renderRevocation(message);
      break;

    case "EVALUATE":
      $("#option_box").show();
      break;
  }

  if (notify.open && messageType !== 'PONG') {
    notify.start();
  }
} // 创建会话消息


renderInMessage.buildChat = function (message) {
  var bodyType = message.body.type;
  var appendInner;
  var content;

  switch (bodyType) {
    case "BUILDING_CHAT":
      renderBroadcast(message.body.content);
      break;

    case "WAITTING":
      renderBroadcast(Constant.waitting_tip.replace("{0}", message.body.content));
      break;

    case "SUCCESS":
      try {
        var buildChatInfo = JSON.parse(message.body.content);
        customer.tmb = buildChatInfo.tmb;
        customer.skn = buildChatInfo.skn;
      } catch (e) {// ignore
      }

      renderBroadcast(Constant.service_info.replace("{0}", message.from.uid));
      break;

    case "TEAM_GREET":
      content = replaceUrl(Emoji.face.toEmoji(message.body.content));
      appendInner = '<div class="service-chat clearfix">' + '<div class="portrait icons"></div><div class="container">' + '<div class="arrow icons"></div><div class="chat-head ell"><span>' + message.datetime + '</span><b>' + (customer.tmb || "") + '</b><b class="left-space">' + message.from.uid + '</b></div><div class="chat-body">' + content + '</div></div></div>';
      break;

    case 'WAITER_GREET':
      content = replaceUrl(Emoji.face.toEmoji(message.body.content));
      appendInner = '<div class="service-chat clearfix">' + '<div class="portrait icons"></div><div class="container">' + '<div class="arrow icons"></div><div class="chat-head ell"><span>' + message.datetime + '</span><b>' + (customer.tmb || "") + '</b><b class="left-space">' + message.from.uid + '</b></div><div class="chat-body">' + content + '</div></div></div>';
      break;
  }

  appendChatBox(message, appendInner);
}; // 会话消息


renderInMessage.message = function (message) {
  var content;
  var appendInner;
  var bodyType = message.body.type;

  switch (bodyType) {
    case "TEXT":
      content = EscapeSequence.filterHtmlJsSpaceEnter(message.body.content);
      appendInner = '<div class="service-chat clearfix">' + '<div class="portrait icons"></div><div class="container">' + '<div class="arrow icons"></div><div class="chat-head ell"><span>' + message.datetime + '</span><b>' + (customer.tmb || "") + '</b><b class="left-space">' + message.from.uid + '</b></div><div class="chat-body">' + replaceUrl(Emoji.face.toEmoji(content)) + '</div></div></div>';
      break;

    case 'TIMEOUT_TIP':
      appendInner = '<div class="robot-chat clearfix">' + '<div class="portrait icons"></div><div class="container">' + '<div class="arrow icons"></div><div class="chat-head ell"><span>' + Common.dateFormat() + '</span><b>客服助手</b></div><div class="chat-body">' + Emoji.face.toEmoji(message.body.content) + '</div></div></div>';
      break;

    case 'TIMEOUT_CLOSE':
      appendInner = '<div class="robot-chat clearfix">' + '<div class="portrait icons"></div><div class="container">' + '<div class="arrow icons"></div><div class="chat-head ell"><span>' + Common.dateFormat() + '</span><b>客服助手</b></div><div class="chat-body">' + Emoji.face.toEmoji(message.body.content) + '</div></div></div>';
      renderBroadcast(Constant.close_tip);
      break;

    case 'IMAGE':
      appendInner = '<div class="service-chat clearfix">' + '<div class="portrait icons"></div><div class="container">' + '<div class="arrow icons"></div><div class="chat-head ell"><span>' + message.datetime + '</span><b>' + (customer.tmb || "") + '</b><b class="left-space">' + message.from.uid + '</b></div><div class="chat-body">' + Common.createImgHtml(message.body.content) + '</div></div></div>';
      break;

    case 'LOGISTICS':
      appendInner = '<div class="service-chat clearfix">' + '<div class="portrait icons"></div><div class="container">' + '<div class="arrow icons"></div><div class="chat-head ell"><span>' + message.datetime + '</span><b>' + (customer.tmb || "") + '</b><b class="left-space">' + message.from.uid + '</b></div><div class="chat-body">' + createLogistics(message.body.content) + '</div></div></div>';
      break;

    case 'GOODS':
      appendInner = '<div class="service-chat clearfix">' + '<div class="portrait icons"></div><div class="container">' + '<div class="arrow icons"></div><div class="chat-head ell"><span>' + message.datetime + '</span><b>' + (customer.tmb || "") + '</b><b class="left-space">' + message.from.uid + '</b></div><div class="chat-body">' + createGoods(message.body.content) + '</div></div></div>';
      break;

    case 'ORDER':
      appendInner = '<div class="service-chat clearfix">' + '<div class="portrait icons"></div><div class="container">' + '<div class="arrow icons"></div><div class="chat-head ell"><span>' + message.datetime + '</span><b>' + (customer.tmb || "") + '</b><b class="left-space">' + message.from.uid + '</b></div><div class="chat-body">' + createOrders(message.body.content) + '</div></div></div>';
      break;

    case 'TIP':
    case 'ROBOT':
      appendInner = '<div class="robot-chat clearfix">' + '<div class="portrait icons"></div><div class="container">' + '<div class="arrow icons"></div><div class="chat-head ell"><span>' + Common.dateFormat() + '</span><b>客服助手</b></div><div class="chat-body">' + Emoji.face.toEmoji(message.body.content) + '</div></div></div>';
      break;

    default:
      appendInner = '<div class="robot-chat clearfix">' + '<div class="portrait icons"></div><div class="container">' + '<div class="arrow icons"></div><div class="chat-head ell"><span>' + Common.dateFormat() + '</span><b>客服助手</b></div><div class="chat-body">' + Emoji.face.toEmoji(message.body.content) + '</div></div></div>';
      renderBroadcast(Constant.close_tip);
  }

  appendChatBox(message, appendInner);
};

renderInMessage.transfer = function (message) {
  var appendInner;
  var bodyType = message.body.type;

  if (bodyType === 'SUCCESS') {
    renderBroadcast(Constant.service_info.replace("{0}", message.from.uid));
    appendInner = '<div class="robot-chat clearfix">' + '<div class="portrait icons"></div><div class="container">' + '<div class="arrow icons"></div><div class="chat-head ell"><span>' + Common.dateFormat() + '</span><b>客服助手</b></div><div class="chat-body">已为您转接客服工号：' + message.from.uid + '， 请输入您要咨询的问题。</div></div></div>';

    try {
      var buildChatInfo = JSON.parse(message.body.content);
      customer.tmb = buildChatInfo.tmb;
      customer.skn = buildChatInfo.skn;
    } catch (e) {// ignore
    }
  }

  appendChatBox(message, appendInner);
};

renderInMessage.broadcast = function (message) {
  var bodyType = message.body.type;

  if (bodyType === 'WAITTING_NO') {
    renderBroadcast(Constant.waitting_tip.replace("{0}", message.body.content));
  }
};

function renderOutMessage(message) {
  var showContainer;

  switch (message.type) {
    case "TEXT":
      showContainer = '<div class="user-chat clearfix">' + '<div class="portrait icons"></div><div class="container">' + '<div class="arrow icons"></div><div class="chat-head ell"><span>' + Common.dateFormat() + '</span><b>我</b></div><div class="chat-body">' + replaceUrl(Emoji.face.toEmoji(message.content)) + '</div></div></div>';
      break;

    case "IMAGE":
      showContainer = '<div class="user-chat clearfix">' + '<div class="portrait icons"></div><div class="container">' + '<div class="arrow icons"></div><div class="chat-head ell"><span>' + Common.dateFormat() + '</span><b>我</b></div><div class="chat-body">' + Emoji.face.toEmoji(message.content) + '</div></div></div>';
      break;

    case "ORDER":
      showContainer = '<div class="user-chat clearfix">' + '<div class="portrait icons"></div><div class="container">' + '<div class="arrow icons"></div><div class="chat-head ell"><span>' + Common.dateFormat() + '</span><b>我</b></div><div class="chat-body">' + createOrders(message.content) + '</div></div></div>';
      break;
  }

  appendChatBox(message, showContainer);
}

function renderBroadcast(message) {
  $("#chat_service_status_msg").text(message);
}

function renderRevocation(message) {
  var pid = message.pid;
  $('div[pid=' + pid + ']').html("<div class='revocation'>" + revocationMessage + "</div>");
}

function renderHistoryRecord(message) {
  if (message) {
    var content = resolverHtyMessage(message);
    var appendInner;

    switch (message.ownerType) {
      case '1':
        appendInner = '<div class="user-chat clearfix">' + '<div class="portrait icons"></div><div class="container">' + '<div class="arrow icons"></div><div class="chat-head ell"><span>' + message.createTime + '</span><b>我</b></div><div class="chat-body">' + content + '</div></div></div>';
        break;

      case '2':
        appendInner = '<div class="service-chat clearfix">' + '<div class="portrait icons"></div><div class="container">' + '<div class="arrow icons"></div><div class="chat-head ell"><span>' + message.createTime + '</span><b>' + message.briefName + '</b><b class="left-space">' + message.waiterCode + '</b></div><div class="chat-body">' + content + '</div></div></div>';
        break;

      default:
        if (message.messageType == '5' || message.messageType == '9') {
          appendInner = '<div class="service-chat clearfix">' + '<div class="portrait icons"></div><div class="container">' + '<div class="arrow icons"></div><div class="chat-head ell"><span>' + message.createTime + '</span><b>' + message.briefName + '</b><b class="left-space">' + message.waiterCode + '</b></div><div class="chat-body">' + content + '</div></div></div>';
        } else {
          appendInner = '<div class="robot-chat clearfix">' + '<div class="portrait icons"></div><div class="container">' + '<div class="arrow icons"></div><div class="chat-head ell"><span>' + message.createTime + '</span><b>客服助手</b></div><div class="chat-body">' + content + '</div></div></div>';
        }

    }

    $("#msg_box").prepend(appendInner);
  }
}

function resolverHtyMessage(message) {
  var content;

  switch (message.messageType) {
    case '2':
      content = createHistoryImgHtml(message.messages);
      break;

    case '10':
      content = createGoods(message.messages);
      break;

    case '11':
      content = createOrders(message.messages);
      break;

    case '12':
      content = createLogistics(message.messages);
      break;

    default:
      content = replaceUrl(Emoji.face.toEmoji(message.messages));
  }

  return content;
}

function createLogistics(message) {
  var o = JSON.parse(message);
  var html = '<div><div style="padding: 2px 0 8px 0; font-size: 13px;"><span>以下是最新物流信息：<span></div>';

  for (var i = 0; i < o.length; i++) {
    html += '<div><span> ' + o[i].lgtime + ' </span></div>' + '<div><span> ' + o[i].lgdesc + ' </span></div>';
  }

  html += '</div>';
  return html;
}

function createGoods(message) {
  var o = JSON.parse(message);
  var html;

  if (isMobile == 0) {
    html = '<a href="' + o.wapUrl + '" target="_blank">' + o.name + '</a>';
  } else {
    html = '<a href="' + o.pcUrl + '" target="_blank">' + o.name + '</a>';
  }

  return html;
}

function createOrders(message) {
  var o = JSON.parse(message); // let o = message;

  var html = "<div class=\"ordercard\">\n                    <ul class=\"sendPrdUl\">\n                        <li class=\"prdCodeLi\">\n                            <div>\n                                <label style=\"padding-left: 8px;\">\u8BA2\u5355\u53F7:</label>\n                                <span style=\"margin-left: -5px;\">".concat(o.orderCode, "</span><br>\n                                <label style=\"padding-left: 8px;\">\u4E0B\u5355\u65F6\u95F4:</label>\n                                <span style=\"margin-left: -5px;\">").concat(o.orderTime, "</span>\n                                <span style=\"float:right\">\n                                    <b>").concat(o.orderStatusInfo, "</b>\n                                </span>\n                            </div>\n                        </li>\n                        <li class=\"prdLi\">\n                            <div class=\"img\">\n                                <img src=\"").concat(o.prdInfo.prdImgUrl, "\" alt=\"\">\n                            </div>\n                            <div class=\"prdLiDiv\">\n                                <a href=\"").concat(o.prdInfo.prdDetailUrl, "\" title=\"").concat(o.prdInfo.prdName, "\" target=\"_blank\" class=\"prdNameP\">").concat(o.prdInfo.prdName, "</a>\n                                <p style=\"color: #999;\">\n                                    <span>\u5546\u54C1\u7F16\u53F7\uFF1A</span>\n                                    <span style=\"margin-left: -0.5rem;\">").concat(o.prdInfo.prdCode, "</span>\n                                </p>  \n                                <p class=\"prdInfoP\">\n                                    <span>\u6570\u91CF\uFF1A</span>\n                                    <span style=\"padding-right: 8px; margin-left: -8px;\">").concat(o.prdInfo.prdAcount, "</span>\n                                    <b>\uFFE5").concat(o.prdInfo.prdPrice, "</b>\n                                </p>\n                            </div>\n                        </li>\n                        <li class=\"prdAmountLi\">\n                            <div style=\"position: relative\">\n                                <span>\u5171</span>\n                                <span style=\"margin-left: -4px;\">").concat(o.orderAcount, "</span>\n                                <span style=\"padding-right: 8px; margin-left: -4px;\">\u4EF6</span>\n                                <span>\u8BA2\u5355\u91D1\u989D\uFF1A</span>\n                                <b style=\"color: #e2231a; margin-left: -12px;\">\uFFE5").concat(o.orderAmount, "</b>\n                            </div>\n                        </li>\n                    </ul>\n                </div>").replace(/\n/gm, "");
  return html;
}

function createHistoryImgHtml(imgurl) {
  return '<img src="' + imgurl + '" style="max-width: 100%; max-height: 300px;" onload="conFitScroll()"/>';
}

function appendHtml(html) {
  $("#msg_box").append(html);
  fitScroll();
}

function appendChatBox(message, html) {
  if (html) {
    var pid = message.pid;

    if (pid) {
      html = '<div class="record" pid="' + pid + '" >' + html + '</div>';
    }

    $("#msg_box").append(html);
  }

  fitScroll();
}

function fitScroll() {
  var h = $("#msg_box").height() - $("#chatDiv").height();

  if (h > 0) {
    $("#chatDiv").scrollTop(h + 40);
  }
}

function replaceUrl(msg) {
  var reg = /((((http?|https?):(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)/gi;
  var result = msg.replace(reg, function (item) {
    var content = item;

    if (item.length > 4 && item.substr(0, 4) !== 'http') {
      item = "//" + item;
    }

    return '<a class="custom-link" href="' + item + '" target="_blank">' + content + '</a>';
  });
  return result;
}

function replaceImgUrl(msg) {
  var arr = [];
  str = str.replace(/(http(s)?:\/\/)?([\w-]+\.)+[\w-]+(\/[\w\-\.\/?%&=]*)?/gi, function (s) {
    arr.push(s);
    return "\x01";
  });
  str = str.replace(/(http(s)?:\/\/)?([\w-]+\.)+[\w-]+(\/[\w\-\.\/?%&=]*)?/g, function (s, a) {
    return '<a class="custom-link" href="' + s + '" target="_blank">' + s + '</a>';
  });
  str = str.replace(/\x01/g, function (s) {
    return arr.shift();
  });
}

var chatRecordMark = {
  first: true
};
var Service = {
  loadChatRecord: function loadChatRecord() {
    var params = {};

    if (chatRecordMark.id) {
      params.id = chatRecordMark.id;
    }

    if (chatRecordMark.more) {
      return;
    }

    Request.jsonp(Api.chatRecord, params, true, function (res) {
      if (res.rc == 0) {
        var records = res.data;
        var len = records.length;

        for (var i = 0; i < len; i++) {
          ChatResolver.renderHistory(records[i]);
        }

        if (chatRecordMark.first) {
          chatRecordMark.first = false;
          $('#load_chat_more').show();
          $('#history_up').show();
        }

        if (!chatRecordMark.id) {
          ChatResolver.toScroll();
        }

        if (len > 0) {
          chatRecordMark.id = records[len - 1].id;
        }

        if (len < 10) {
          chatRecordMark.more = true;
          $('.load-chat-more').html("没有更多");
        }
      }
    });
  }
};
/**
 * @author pengzq
 * @since 2019.06.03
 * @version v1.0
 */

var HpClient = function HpClient(opts) {
  var customer = opts.customer;
  opts.active = false;
  connect();

  function connect() {
    var connectPacket = {
      type: 'AUTH',
      ts: 'POLLING',
      ttc: customer.ttc,
      skc: customer.skc,
      skn: customer.skn,
      gc: customer.gc,
      from: {
        idy: "CUSTOMER"
      },
      body: {
        type: 'LOGIN'
      }
    };
    doSend(connectPacket, function (res) {
      initEventHandler();
    });
  }

  function initEventHandler() {
    ChatResolver.render(ChatResolver.type.broadcast, Constant.build_success);
    poll();
    opts.active = true;
    bindMessageElement();
  }

  function poll() {
    var pollPacket = {
      type: "POLL",
      ts: 'POLLING',
      ttc: customer.ttc,
      skc: customer.skc,
      skn: customer.skn,
      gc: customer.gc,
      to: {
        idy: "WAITER"
      },
      from: {
        uid: customer.cc,
        name: customer.cn,
        idy: "CUSTOMER"
      },
      body: {
        type: "TEXT"
      }
    };
    $.ajax({
      type: "GET",
      url: opts.url,
      data: {
        packet: JSON.stringify(pollPacket),
        t: new Date().getTime()
      },
      dataType: 'json',
      xhrFields: {
        withCredentials: true
      },
      success: function success(res) {
        if (res && res instanceof Array) {
          for (var i = 0; i < res.length; i++) {
            ChatResolver.render(ChatResolver.type.inMessage, res[i]);
          }
        }

        if (opts.active) {
          setTimeout(poll(), 1000);
        }
      },
      error: function error(res) {
        console.log("请求失败： " + res);
      }
    });
  }

  var bindMessageElement = function bindMessageElement() {
    $(document).delegate("#message_area", "keydown", function (event) {
      if (!(event.ctrlKey || event.metaKey || event.altKey)) {
        $("#message-area").focus();
      }

      if (event.which === 13) {
        var message = '';
        message = $("#message_area").val();

        if (message === '') {
          Pdialog.showDialog({
            content: "消息不能为空"
          });
          return false;
        }

        if (message && message.length > 300) {
          Pdialog.showDialog({
            content: "消息不能超过300字"
          });
          return false;
        }

        if (message && message.length <= 300) {
          $("#message_area").val("");
          message = EscapeSequence.filterHtmlJsSpaceEnter(message);

          if (isMobile === 1) {
            $("#message_area").focus();
          } else {
            $("#message_area").blur();
          }

          sendMessage(message, 'TEXT');
          ChatResolver.render(ChatResolver.type.outMessage, {
            content: message,
            type: 'TEXT'
          });
        }

        event.preventDefault();
        return false;
      }
    });
    $(document).delegate("#send_msg_btn", "click", function () {
      var message = '';
      message = $("#message_area").val();

      if (message === '') {
        Pdialog.showDialog({
          content: "消息不能为空"
        });
        return false;
      }

      if (message && message.length > 300) {
        Pdialog.showDialog({
          content: "消息不能超过300字"
        });
        return false;
      }

      if (message && message.length <= 300) {
        $("#message_area").val("");
        message = EscapeSequence.filterHtmlJsSpaceEnter(message);

        if (isMobile === 1) {
          $("#message_area").focus();
        } else {
          $("#message_area").blur();
        }

        sendMessage(message, 'TEXT');
        ChatResolver.render(ChatResolver.type.outMessage, {
          content: message,
          type: 'TEXT'
        });
      }

      event.preventDefault();
      return false;
    });
  };

  function sendMessage(message, bodyType) {
    var sendPacket = {
      type: 'MESSAGE',
      ts: 'POLLING',
      ttc: customer.ttc,
      skc: customer.skc,
      skn: customer.skn,
      gc: customer.gc,
      to: {
        idy: 'WAITER'
      },
      from: {
        uid: customer.cc,
        name: customer.cn,
        idy: "CUSTOMER"
      },
      body: {
        type: bodyType,
        content: message
      }
    };
    doSend(sendPacket, function (res) {
      console.log("消息发送成功!" + JSON.stringify(sendPacket));
    });
  }

  function doSend(packet, succ, fail) {
    if (packet) {
      packet.pid = Common.getPid();
      $.ajax({
        type: "GET",
        url: opts.url,
        data: {
          packet: JSON.stringify(packet),
          t: new Date().getTime()
        },
        dataType: 'json',
        xhrFields: {
          withCredentials: true
        },
        success: function success(res) {
          succ(res);
        },
        error: function error(res) {
          fail && fail instanceof Function && fail(res);
          console.log("发送消息失败：{}", JSON.stringify(res));
        }
      });
    }
  }

  return {
    sendImg: function sendImg(img, message, type) {
      sendMessage(message, type);
      ChatResolver.render(ChatResolver.type.outMessage, {
        content: img,
        type: 'IMAGE'
      });
    },
    sendMsg: function sendMsg(message, type) {
      sendMessage(message, type);
      ChatResolver.render(ChatResolver.type.outMessage, {
        content: message,
        type: type
      });
    },
    close: function close() {
      opt.active = false;
      var closePacket = {
        type: 'CLOSE',
        ts: 'POLLING',
        ttc: customer.ttc,
        skc: customer.skc,
        skn: customer.skn,
        gc: customer.gc,
        to: {
          idy: 'WAITER'
        },
        from: {
          uid: customer.cc,
          name: customer.cn,
          idy: "CUSTOMER"
        }
      };
      doSend(closePacket, function (res) {
        console.log("关闭!");
      });
    }
  };
};
/**
 * @author pengzq
 * @since 2019.06.03
 * @version v1.0
 */


var WsClient = function WsClient(opts) {
  var wsPING;
  var ws;
  var customer = opts.customer;
  var isError = false;
  var isClose = true;
  connect();

  function connect() {
    var connectPacket = {
      type: "AUTH",
      ts: "WEBSOCKET",
      from: {
        idy: "CUSTOMER"
      },
      body: {
        type: "LOGIN"
      }
    };
    var url = opts.url + "?packet=" + JSON.stringify(connectPacket) + "&t=" + new Date().getTime();
    ws = new WebSocket(url);
    wsEventHandler();
    bindInputMessageBox();
  }

  function wsEventHandler() {
    ws.onopen = function () {
      isError = false;
      ChatResolver.render(ChatResolver.type.broadcast, Constant.build_success);
      clearInterval(wsPING);
      wsPING = setInterval(function () {
        var pingPacket = {
          type: 'PING',
          ts: 'WEBSOCKET',
          body: {
            type: 'TEXT',
            content: "PING"
          }
        };
        doSend(pingPacket);
      }, 30000);
    };

    ws.onmessage = function (data) {
      data = JSON.parse(data.data);

      if (data.type == 'RE_LOGIN') {
        isClose = true;
        clearInterval(wsPING);
        ChatResolver.renderBroadcast("已在其他打开咨询，当前会话被关闭");
      } else {
        ChatResolver.render(ChatResolver.type.inMessage, data);
      }
    };

    ws.onerror = function (data) {
      isError = true;
      ChatResolver.render(ChatResolver.type.broadcast, "糟糕，网络错误，工程师正在维护中...");
    };

    ws.onclose = function () {
      if (isClose) {
        return;
      }

      if (!isError) {
        ChatResolver.render(ChatResolver.type.broadcast, "糟糕，网络连接断开");
      }

      reconnect();
    };
  }

  function bindInputMessageBox() {
    $(document).delegate("#message_area", "keydown", function (event) {
      if (!(event.ctrlKey || event.metaKey || event.altKey)) {
        $("#message-area").focus();
      }

      if (event.which === 13) {
        $("#send_msg_btn").click();
        event.preventDefault();
        return false;
      }
    });
    $("#send_msg_btn").on("click", function () {
      var message = $("#message_area").val();

      if (message === '') {
        Pdialog.showDialog({
          content: "消息不能为空"
        });
        return false;
      }

      if (message && message.length > 300) {
        Pdialog.showDialog({
          content: "消息不能超过300字"
        });
        return false;
      }

      if (message && message.length <= 300) {
        $("#message_area").val("");
        message = EscapeSequence.filterHtmlJsSpaceEnter(message);

        if (isMobile === 1) {
          $("#message_area").focus();
        } else {
          $("#message_area").blur();
        }

        sendMessage(message, 'TEXT');
        ChatResolver.render(ChatResolver.type.outMessage, {
          content: message,
          type: 'TEXT'
        });
      }

      event.preventDefault();
      return false;
    });
  }

  function sendMessage(message, bodyType) {
    var sendPacket = {
      type: 'MESSAGE',
      ts: 'WEBSOCKET',
      from: {
        uid: customer.cc,
        name: customer.cn,
        idy: "CUSTOMER"
      },
      body: {
        type: bodyType,
        content: message
      }
    };
    doSend(sendPacket);
  }

  function doSend(packet) {
    if (packet) {
      if (ws.readyState === 1) {
        packet.pid = Common.getPid();
        packet.to = {
          idy: 'WAITER'
        };
        ws.send(JSON.stringify(packet));
      } else {
        reconnect();
      }
    }
  }

  function reconnect() {
    if (ws.readyState === 3) {
      clearInterval(wsPING);
      setTimeout(connect, 5000);
    }
  }

  return {
    sendImg: function sendImg(img, message, type) {
      sendMessage(message, type);
      ChatResolver.render(ChatResolver.type.outMessage, {
        content: img,
        type: 'IMAGE'
      });
    },
    sendMsg: function sendMsg(message, type) {
      sendMessage(message, type);
      ChatResolver.render(ChatResolver.type.outMessage, {
        content: message,
        type: type
      });
    },
    close: function close() {
      if (ws.readyState == 1) {
        ws.close();
      }
    }
  };
};

var Pview = initPView();
var pImgPro = {};

function initPView() {
  $(".p-view").on("click", "img", function (e) {
    showPzqImgView(this);
  });
}

function showPzqImgView(imgPro) {
  var winWidth = $(window).width();
  var winHeight = $(window).height();
  var imgWidth = imgPro.naturalWidth;
  var imgHeight = imgPro.naturalHeight;

  if (winWidth < imgWidth) {
    imgWidth = winWidth;
    imgHeight = imgHeight - parseInt((imgWidth - winWidth) * (imgWidth / imgHeight));
  }

  if (winHeight < imgHeight) {
    imgHeight = winHeight;
    imgWidth = imgWidth - parseInt((imgHeight - winHeight) * (imgHeight / imgWidth));
  }

  pImgPro.src = imgPro.src;
  pImgPro.rotate = 0;
  pImgPro.change = false;
  pImgPro.scale = 100;
  pImgPro.height = imgHeight;
  pImgPro.width = imgWidth;
  pImgPro.top = parseInt(winHeight / 2 - pImgPro.height / 2);
  pImgPro.left = parseInt(winWidth / 2 - pImgPro.width / 2);
  var pImgViewMaskWrapper = '<div id="p_img_mask_wrapper"><div id="p-view-close-container"><div id="p-view-close"></div></div>';
  var pImgContainer = '<div id="p_img_container" style="left:' + pImgPro.left + 'px; top: ' + pImgPro.top + 'px;">';
  var pImgEl = '<img id="p_img" src="' + pImgPro.src + '" style="width: ' + pImgPro.width + 'px;" />';
  var pScaleTip = '<div id="p_img_tip"></div>';
  var pImgOpts = '<div id="p_img_opts">' + '<span id="p_img_plus_btn" title="放大"></span>' + '<span id="p_img_minus_btn" title="缩小"></span>' + '<span id="p_img_rotate_btn" title="旋转"></span>' + '<span id="p_img_reset_btn" title="重置"></span>' + '</div>';
  var endTag = "</div>";
  $('#p_img_mask_wrapper').remove();
  var appendHtml = pImgViewMaskWrapper + pImgContainer + pImgEl + endTag + pScaleTip + pImgOpts + endTag;
  $('body').append(appendHtml);
  initPViewEvent();
}

function initPViewEvent() {
  $('#p_img_mask_wrapper').unbind("click").bind("click", function () {
    Common.stopPropagation(event);
    $('#p_img_mask_wrapper').remove();
    pImgPro = {};
  });
  $('#p_img_container').unbind("click").bind("click", function () {
    Common.stopPropagation(event);
  });
  $('#p_img_plus_btn').unbind("click").bind("click", function () {
    Common.stopPropagation(event);
    pImgPro.scale += 10;

    if (pImgPro.scale >= 260) {
      pImgPro.scale = 260;
      $('#p_img_tip').html('已经是最大比例' + pImgPro.scale + '%').finish().fadeIn().delay(1500).fadeOut();
    } else {
      var scale = pImgPro.scale / 100;
      $('#p_img_tip').html(pImgPro.scale + '%').finish().fadeIn().delay(1500).fadeOut();
      $('#p_img').width(pImgPro.width * scale);
    }

    if (!pImgPro.change) {
      pImgToCenter();
    }
  });
  $('#p_img_minus_btn').unbind("click").bind("click", function () {
    Common.stopPropagation(event);
    pImgPro.scale -= 10;

    if (pImgPro.scale <= 0) {
      pImgPro.scale = 10;
      $('#p_img_tip').html('已是最小比例' + pImgPro.scale + "%").finish().fadeIn().delay(1500).fadeOut();
    } else {
      var scale = pImgPro.scale / 100;
      $('#p_img_tip').html(pImgPro.scale + '%').finish().fadeIn().delay(1500).fadeOut();
      $('#p_img').width(pImgPro.width * scale);
    }

    if (!pImgPro.change) {
      pImgToCenter();
    }
  });
  $('#p_img_rotate_btn').unbind("click").bind("click", function () {
    Common.stopPropagation(event);
    pImgPro.rotate = pImgPro.rotate + 90;

    if (pImgPro.rotate > 360) {
      pImgPro.rotate = 90;
    }

    $('#p_img_tip').html('旋转' + pImgPro.rotate + "度").finish().fadeIn().delay(1500).fadeOut();
    $('#p_img').css('transform', 'rotate(' + pImgPro.rotate + 'deg)');
  });
  $('#p_img_reset_btn').unbind("click").bind("click", function (event) {
    Common.stopPropagation(event);
    $('#p_img_tip').html('重置').finish().fadeIn().delay(1500).fadeOut();
    $('#p_img').width(pImgPro.width);
    $('#p_img').css('transform', 'rotate(0deg)');
    pImgToCenter();
    pImgPro.scale = 100;
    pImgPro.change = false;
  });
  var $pImg = $('#p_img_container');
  $pImg.bind("mousedown", function (downEvent) {
    Common.stopPropagation(downEvent);

    if (downEvent.button == 0) {
      $('#p_img_opts').hide();
      var imgOffset = $pImg.offset();
      var offsetX = downEvent.pageX - imgOffset.left;
      var offsetY = downEvent.pageY - imgOffset.top;
      $pImg.unbind('mousemove').bind('mousemove', function (moveEvent) {
        pImgPro.change = true;
        $pImg.css('left', moveEvent.pageX - offsetX + 'px');
        $pImg.css('top', moveEvent.pageY - offsetY + 'px');
        return false;
      });
      $pImg.unbind('mouseup').bind('mouseup', function () {
        $pImg.unbind('mousemove');
        $('#p_img_opts').show();
        return;
      });
    }

    return false;
  });
}

function pImgToCenter() {
  var winWidth = $(window).width();
  var winHeight = $(window).height();
  var imgW = $('#p_img').width();
  var imgH = $('#p_img').height();
  $('#p_img_container').css({
    "left": winWidth / 2 - imgW / 2 + "px",
    "top": winHeight / 2 - imgH / 2
  });
}

var face = {
  em: undefined,
  toEmoji: undefined,
  show: undefined,
  hide: undefined
};
var Emoji = {
  face: createEmoji()
};

function render_nav(obj, options) {
  $(obj).append('<div class="emoji-inner"><ul class="emoji-nav"></ul><div class="emoji-content"></div></div>');

  if (!options.showbar) {
    $(obj).addClass('no-bar');
    return;
  }

  ;

  var navs = _.reduce(options.category, function (items, item) {
    var citem = _.find(options.data, function (da) {
      return da.typ == item;
    });

    return items + '<li data-name="' + item + '">' + citem.nm + '</li>';
  }, '');

  $(obj).find('.emoji-nav').empty().append(navs);
}

; //渲染表情

function render_emoji(obj, options, typ) {
  var list = _.find(options.data, function (item) {
    return item.typ == typ;
  });

  if (!list) {
    list = options.data[0];
  }

  ;

  var imgs = _.reduce(list.items, function (items, item) {
    if (item) {
      return items + '<img title="' + item.name + '" src="' + options.path + item.src + '" data-name="' + item.name + '" data-src="' + options.path + item.src + '">';
    } else {
      return items;
    }
  }, '');

  $(obj).find('.emoji-content').empty().append(imgs);
  $(obj).find('.emoji-nav li[data-name=' + list.typ + ']').addClass('on').siblings().removeClass("on");
}

; //切换元素

function switchitem(obj, options) {
  $(obj).on(options.trigger, '.emoji-nav > li', function () {
    render_emoji(obj, options, $(this).attr('data-name'));
    return false;
  });
  $(obj).on('click', '.emoji-content > img', function () {
    options.insertAfter({
      name: $(this).attr('data-name'),
      src: $(this).attr('data-src')
    });
  });
}

;

function togglew(obj, option) {
  $(obj).on('click', '.emoji-tbtn', function () {
    $(obj).find('.emoji-inner').toggle();
    return false;
  });
  $(document).click(function () {
    $(obj).hide();
    $(obj).find('.emoji-inner').hide();
  });
}

; //jq插件

function initEmoji(opt) {
  var emojiContainer = $('#emoji_container');
  var options = $.extend({}, getData(), opt || {});

  face.hide = function () {
    emojiContainer.hide();
    emojiContainer.find('.emoji-inner').hide();
  };

  face.show = function () {
    emojiContainer.show();
    emojiContainer.find('.emoji-inner').show();
  };

  return emojiContainer.each(function () {
    if (!emojiContainer.find('.val').length > 0) {
      return false;
    }

    ; //初始化tab

    render_nav(emojiContainer, options); //初始化表情

    render_emoji(emojiContainer, options); //切换表情

    switchitem(emojiContainer, options); //点击显示表情

    togglew(emojiContainer, options);
  });
}

; // 插入、修改表情图片

function insertContent(myValue, t) {
  var messageArea = $('#message_area');
  var $t = messageArea[0];

  if (window.getSelection) {
    var startPos = $t.selectionStart;
    var endPos = $t.selectionEnd;
    var scrollTop = $t.scrollTop;
    $t.value = $t.value.substring(0, startPos) + myValue + $t.value.substring(endPos, $t.value.length);
    messageArea.focus();
    $t.selectionStart = startPos + myValue.length;
    $t.selectionEnd = startPos + myValue.length;
    $t.scrollTop = scrollTop;

    if (arguments.length == 2) {
      $t.setSelectionRange(startPos - t, $t.selectionEnd + t);
      messageArea.focus();
    }
  } else if (document.selection) {
    messageArea.focus();
    var sel = document.selection.createRange();
    sel.text = myValue;
    messageArea.focus(); //moveStart

    sel.moveStart('character', -l);
    var wee = sel.text.length;

    if (arguments.length == 2) {
      var l = $t.value.length;
      sel.moveEnd("character", wee + t);
      t <= 0 ? sel.moveStart("character", wee - 2 * t - myValue.length) : sel.moveStart("character", wee - t - myValue.length);
      sel.select();
    }
  } else {
    messageArea.value += myValue;
    messageArea.focus();
  } // 显示发送按钮


  $("#input-add-field").hide();
  $("#send_msg_btn").show();
}

function createEmoji() {
  var opts = {
    insertAfter: function insertAfter(item) {
      // insertContent(document.getElementById('message_area'), item.src, '[:' + item.name + ':]')
      insertContent('[:' + item.name + ':]');
    },
    path: "images/"
  };
  initEmoji(opts);
  var arr = getEmoji();

  face.toEmoji = function (str) {
    var emojis = [];

    _.each(arr, function (ele) {
      if (ele !== null) {
        emojis.push(ele.name);
      }
    });

    var regex = new RegExp('\\[:(' + (emojis.length > 1 ? emojis.join("|") : emojis + "") + '):\\]', 'g');

    var emoji = function emoji(key) {
      key = key.replace("[:", "").replace(":]", "");

      var list = _.filter(arr, function (ele) {
        if (ele == null) {
          return false;
        }

        return ele.name == key;
      });

      return '<img name="[:' + key + ':]" src="' + opts.path + list[0].src + '" alt="' + key + '" style="width: 25px;height: 25px;cursor: pointer;vertical-align: middle;"/>';
    };

    return str.replace(regex, emoji);
  };

  return face;
}

function getEmoji(category) {
  var datas;

  if (category) {
    datas = this.data = _.filter(getData().data, function (ele) {
      return _.contains(category, ele.typ);
    });
  } else {
    datas = getData().data;
  }

  var i = 0,
      len = datas.length;
  var arr = [];

  for (; i < len; i++) {
    var item = datas[i].items,
        j = 0,
        len1 = item.length;

    for (; j < len1; j++) {
      arr.push(item[j]);
    }
  }

  return arr;
}

function getData() {
  return {
    data: [{
      "typ": "EmojiCategory-People",
      "nm": "人物",
      "items": [{
        "name": "笑脸",
        "src": "face/笑脸.png"
      }, {
        "name": "开心",
        "src": "face/开心.png"
      }, {
        "name": "大笑",
        "src": "face/大笑.png"
      }, {
        "name": "爱心",
        "src": "face/爱心.png"
      }, {
        "name": "飞吻",
        "src": "face/飞吻.png"
      }, {
        "name": "调皮",
        "src": "face/调皮.png"
      }, {
        "name": "讨厌",
        "src": "face/讨厌.png"
      }, {
        "name": "笑哭",
        "src": "face/笑哭.png"
      }, {
        "name": "流泪",
        "src": "face/流泪.png"
      }, {
        "name": "坏笑",
        "src": "face/坏笑.png"
      }, {
        "name": "流汗",
        "src": "face/流汗.png"
      }, {
        "name": "汗颜",
        "src": "face/汗颜.png"
      }, {
        "name": "尴尬",
        "src": "face/尴尬.png"
      }, {
        "name": "流泪",
        "src": "face/流泪.png"
      }, {
        "name": "冷酷",
        "src": "face/冷酷.png"
      }, {
        "name": "惊恐",
        "src": "face/惊恐.png"
      }, {
        "name": "惊悚",
        "src": "face/惊悚.png"
      }, {
        "name": "惊讶",
        "src": "face/惊讶.png"
      }, {
        "name": "大惊",
        "src": "face/大惊.png"
      }, {
        "name": "大闹",
        "src": "face/大闹.png"
      }, {
        "name": "发呆",
        "src": "face/发呆.png"
      }, {
        "name": "犯困",
        "src": "face/犯困.png"
      }, {
        "name": "心碎",
        "src": "face/心碎.png"
      }, {
        "name": "酷",
        "src": "face/酷.png"
      }, {
        "name": "生气",
        "src": "face/生气.png"
      }, {
        "name": "闭嘴",
        "src": "face/闭嘴.png"
      }, {
        "name": "睡着",
        "src": "face/睡着.png"
      }, {
        "name": "奋斗",
        "src": "face/奋斗.png"
      }, {
        "name": "愤怒",
        "src": "face/愤怒.png"
      }, {
        "name": "瞌睡",
        "src": "face/瞌睡.png"
      }, {
        "name": "难过",
        "src": "face/难过.png"
      }, {
        "name": "天使",
        "src": "face/天使.png"
      }, {
        "name": "无聊",
        "src": "face/无聊.png"
      }, {
        "name": "骂人",
        "src": "face/骂人.png"
      }, {
        "name": "点赞",
        "src": "face/点赞.png"
      }, {
        "name": "懵逼",
        "src": "face/懵逼.png"
      }, {
        "name": "白眼",
        "src": "face/白眼.png"
      }, {
        "name": "恶魔",
        "src": "face/恶魔.png"
      }, {
        "name": "感冒",
        "src": "face/感冒.png"
      }, {
        "name": "爱你",
        "src": "face/爱你.png"
      }, {
        "name": "呕吐",
        "src": "face/呕吐.png"
      }, {
        "name": "呲牙",
        "src": "face/呲牙.png"
      }]
    }],
    path: 'images/',
    category: ['EmojiCategory-People'],
    showbar: true,
    trigger: 'click',
    insertAfter: function insertAfter() {}
  };
}

var suggest = {};
var ChatWap = {
  startChat: function startChat() {
    initLoadGoods();
    initLoadRecommend();
    initEvent();
    initOrderList();
  }
};
var timer_comment;
var orderCode,
    arrOrder = [],
    pageNum = 1,
    off_on = false,
    //分页开关(滚动加载方法 1 中用的)
timers = null; //定时器(滚动加载方法 2 中用的)

function showAndhideTips(content) {
  $(".s-tips").html(content);
  $(".m-sbt-suc").show();

  if (timer_comment) {
    clearTimeout(timer_comment);
  }

  timer_comment = setTimeout(function () {
    $(".s-tips").html("");
    $(".m-sbt-suc").hide();
  }, 1500);
}

function resetSuggest() {
  $("#option_box").hide();
  $(".chat-menu-footer").hide();
  $("#inner_sug").val("");
  $(".ay-star-ul li").css("background-image", "url('/images/chatwap/ic.png')");
}

function initEvent() {
  $(document).click(function (e) {
    // 判断除“footerActionBar”以外->隐藏
    if (e.target.id !== 'footerActionBar') {
      $("#emoji_container").hide();
      $("#emoji_container").find('.emoji-inner').hide();
      $(".chat-menu-footer").hide(); //底部菜单栏
    }
  }); // 底部区域-点击事件

  $('#footerActionBar').on('click', function (e) {
    //阻止当前区域->冒泡
    Common.stopPropagation(event); // 判断表情内容是否为show

    if ($("#emoji_container").is(':visible')) {
      $("#emoji_container").show();
    } // 判断底部菜单栏是否为show


    if ($(".chat-menu-footer").is(':visible')) {
      $(".chat-menu-footer").show();
    }
  }); // 初始化表情内容

  $(".click-emoji").on('click', function () {
    $("#emoji_container").toggle();
    $("#emoji_container .emoji-inner").show(); //表情内容显示<-->底部菜单栏隐藏

    $(".chat-menu-footer").hide();
  }); // 初始化+事件

  $("#input-add-field").on('click', function () {
    $(".chat-menu-footer").toggle();
    $("#emoji_container").hide();
  }); // 显示评论

  $("#click-star").on('click', function () {
    if (customer.cid == suggest.chatId) {
      showAndhideTips("已评价！");
      return false;
    }

    if (!customer.cid) {
      showAndhideTips("请咨询后再评价！");
      return false;
    }

    $("#option_box").show();
    $(".chat-menu-footer").hide();
  }); //开始评价

  $(".ay-star-ul li").unbind("click").bind("click", function () {
    var index = $(this).index();
    suggest.opinion = index + 1;
    $(".ay-star-ul").find("li").css("background-image", "url('/images/chatwap/ic.png')");

    for (var i = 0; i <= index; i++) {
      if (index < 3) {
        $('.textarea-div').css({
          'display': 'block'
        });
      } else {
        $('.textarea-div').css({
          'display': 'none'
        });
      }

      $(".ay-star-ul").find("li").eq(i).css("background-image", "url('/images/chatwap/iced.png')");
    }
  }); // 评论框的关闭按钮

  $(".ay-ms-cut").on('click', function (e) {
    resetSuggest();
  }); // 提交评价

  $("#submit_sug").on('click', function () {
    if (customer.cid == suggest.chatId) {
      showAndhideTips("已评价！");
      resetSuggest();
      return false;
    }

    if (!customer.cid) {
      showAndhideTips("请咨询后再评价！");
      resetSuggest();
      return false;
    }

    suggest.suggest = $("#inner_sug").val(); //建议

    suggest.chatId = customer.cid; //客服id

    if (!suggest.opinion || !suggest.suggest && suggest.opinion && (suggest.opinion == "1" || suggest.opinion == "2")) {
      showAndhideTips("请选择或者输入建议！");
      return false;
    }

    if (suggest.opinion && (suggest.opinion == "1" || suggest.opinion == "2") && $.trim(suggest.suggest).length < 10) {
      showAndhideTips("请填写您的建议且建议内容至少10个字!");
      $("#inner_sug").focus();
      return false;
    } //转义html to encode


    if ($.trim(suggest.suggest)) {
      suggest.suggest = EscapeSequence.filterHtmlJsSpaceEnter(suggest.suggest);
    }

    if (suggest.chatId && suggest.opinion) {
      Request.jsonp(Api.opinion, suggest, true, function (res) {
        if (res && res.rc == 0) {
          suggest.opinion = undefined;
          showAndhideTips("评价成功！");
        } else {
          showAndhideTips(res.rm);
          resetSuggest();
          return false;
        }
      });
    } else {
      showAndhideTips("请完善评价信息！");
    }
  }); //当输入框获取焦点，显示发送按钮

  $("#message_area").focus(function (e) {
    if ($("#emoji_container").is(':visible')) {
      $("#emoji_container").show();
    }

    if ($(".chat-menu-footer").is(':visible')) {
      $(".chat-menu-footer").show();
    }

    $(".chat-menu-footer").hide();
    $("#emoji_container").hide();
    var messageValue = $("#message_area").val();

    if (messageValue) {
      $("#input-add-field").hide();
      $("#send_msg_btn").show();
    } else {
      $("#send_msg_btn").hide();
      $("#input-add-field").show();
    }
  }); //当输入框失去焦点，显示发送按钮

  $("#message_area").blur(function () {
    var messageValue = $("#message_area").val();

    if (messageValue) {
      $("#input-add-field").hide();
      $("#send_msg_btn").show();
    } else {
      $("#send_msg_btn").hide();
      $("#input-add-field").show();
    }
  });
  $('#message_area').on('keyup', function () {
    var messageValue = $("#message_area").val();

    if (messageValue) {
      $("#input-add-field").hide();
      $("#send_msg_btn").show();
    } else {
      $("#send_msg_btn").hide();
      $("#input-add-field").show();
    }
  }); //点击图片事件

  $("#set-file").unbind("click").bind('click', function () {
    $("#upload_file").click();
    return false;
  }); // 上传图片

  $(document).delegate("#upload_file", "change", function () {
    var fileObj = $(this);

    if (fileObj.val().length < 1) {
      return;
    }

    var file = fileObj[0].files['0'];

    if (file) {
      if (file.type.indexOf("image/") == -1) {
        Pdialog.showDialog({
          content: "请上传图片"
        });
        return;
      }

      if (file.size.toFixed(2) > 6 * 1024 * 1024) {
        Pdialog.showDialog({
          content: "请上传小于6M的图片"
        });
        return;
      } //获取照片方向角属性，用户旋转控制


      EXIF.getData(file, function () {
        EXIF.getAllTags(this);
        Orientation = EXIF.getTag(this, 'Orientation');
      });
      var oReader = new FileReader();

      oReader.onload = function (e) {
        var image = new Image();
        image.src = e.target.result;

        image.onload = function () {
          var expectWidth = this.naturalWidth;
          var expectHeight = this.naturalHeight;

          if (this.naturalWidth > this.naturalHeight && this.naturalWidth > 800) {
            expectWidth = 800;
            expectHeight = expectWidth * this.naturalHeight / this.naturalWidth;
          } else if (this.naturalHeight > this.naturalWidth && this.naturalHeight > 1200) {
            expectHeight = 1200;
            expectWidth = expectHeight * this.naturalWidth / this.naturalHeight;
          }

          var canvas = document.createElement("canvas");
          var ctx = canvas.getContext("2d");
          canvas.width = expectWidth;
          canvas.height = expectHeight;
          ctx.drawImage(this, 0, 0, expectWidth, expectHeight);
          var base64 = null;
          base64 = canvas.toDataURL("image/jpeg", 0.8);
          Common.uploadImgB64(Api.uploadImgb64, base64, function (res) {
            if (res.rc == 0) {
              var img = Common.createImgHtml(res.imgurl, false);
              io.sendImg(img, res.imgurl, "IMAGE");
            } else {
              showAndhideTips(res.msg);
            }

            $("#set-file").data("isUploading", false);
            $("#upload_file").after($("#upload_file").clone().val('')).remove();
          });
        };
      };

      oReader.readAsDataURL(file);
    }
  }); // 点击我的订单

  $('.marks_info .mark_order').on('click', function (e) {
    $(".marks_info").parents(".app-main").siblings(".myorder").show();
    $(".marks_info").parents('.app-main').css("opacity", "0.1");
  });
  $('.myorder .close_myorder').on('click', function (e) {
    $(".marks_info").parents(".app-main").siblings(".myorder").hide();
    $(".marks_info").parents('.app-main').css("opacity", "1");
  }); // 发送链接

  $('.myorder_main').on('click', 'ul li .send-order', function (e) {
    var index = $(this).parent().parent().parent().parent().index();
    var prdindex = $(this).attr('data-index');
    var data = arrOrder[index];
    var prddata = arrOrder[index].prdInfoList[prdindex];
    var sendData = {
      "orderCode": data.orderId,
      "orderTime": data.orderTime,
      "orderAmount": data.orderAmount,
      "orderStatusInfo": data.orderStatusInfo,
      "prdInfo": {
        "prdImgUrl": prddata.prdImgUrl,
        "prdName": prddata.prdName,
        "prdAcount": prddata.prdAcount,
        "prdPrice": prddata.prdPrice,
        "prdDetailUrl": prddata.prdDetailUrl,
        "prdCode": prddata.prdCode
      },
      "orderAcount": data.prdInfoList.length
    };
    io.sendMsg(JSON.stringify(sendData), 'ORDER');
    $(".marks_info").parents(".app-main").siblings(".myorder").hide();
    $(".marks_info").parents('.app-main').css("opacity", "1");
  }); // 查看物流

  $('.myorder_main').on('click', 'ul li .view', function (e) {
    var index = $(this).parents().parents().index();
    var data = arrOrder[index];

    if (!$(this).hasClass('active')) {
      $(this).addClass('active').parents().siblings('.logisticsList').show();
      $(this).parents().parents().siblings().children('.logisticsList').hide();
      initLogistics(data);
    } else {
      $(this).removeClass('active').parents().siblings('.logisticsList').hide();
    }
  });
}

function initChat() {
  if (customer && customer.real == 1) {
    Service.loadChatRecord();
    $("#load_chat_more").bind("click", function () {
      Service.loadChatRecord();
    });
  } else {
    $('.load-chat-more').hide();
    $('.history-up').hide();
  }
}

function initLoadGoods() {
  var gc = Common.getUrlParams().gc;

  if (gc) {
    Request.jsonp(Api.goodsDetail, {
      gc: gc,
      plat: 1
    }, false, function (res) {
      if (res.rc == 0) {
        var goodsDetail;

        if (res.data) {
          goodsDetail = JSON.parse(res.data);
          console.log(res.data, 295);
        }

        if (goodsDetail && goodsDetail.items && goodsDetail.items.length > 0) {
          createGoodsHtml(goodsDetail.items[0]);
          return;
        }
      }
    });
  }
}

function initLoadRecommend() {
  Request.jsonp(Api.recommendList, {}, true, function (res) {
    if (res.rc == 0) {
      if (res.data) {
        var _html = createRecommendHtml(res.data);

        if (_html.length > 0) {
          $("#mark_list").html(_html);
          $("div.marks_info").show();
        }
      }
    }
  });
}

function createRecommendHtml(recommends) {
  var _html = [];

  if (recommends && recommends.length > 0) {
    var recommend;

    for (var i = 0; i < recommends.length; i++) {
      recommend = recommends[i];

      if (recommend) {
        _html.push("<a href='" + (recommend.recoUrl ? recommend.recoUrl : "javascript://;") + "' target='_blank'>" + (recommend.recoName ? recommend.recoName : "") + "</a>");
      }
    }
  }

  return _html.join("");
} // 移动到最后


function setCaretPosition(ele) {
  ele.blur();
  ele.focus();
  var len = ele.value.length;

  if (document.selection) {
    var sel = ele.createTextRange();
    sel.moveStart('character', len);
    sel.collapse();
    sel.select();
  } else {
    ele.selectionStart = ele.selectionEnd = len;
  }
}

function createGoodsHtml(item) {
  var url, goodsUrl;

  if (Common.isMobile()) {
    url = item.wapDetailUrl;
  } else {
    url = item.pcDetailUrl;
  }

  if (item.path && item.path.charAt(0) === '/') {
    goodsUrl = 'https://p1.lefile.cn' + item.path;
  } else {
    goodsUrl = 'https://p1.lefile.cn/' + item.path;
  }

  var html = '<div class="goods-info">' + '<div class="goods-container">' + '<div class="goods-img"><img id="goods_pic" src="' + goodsUrl + '" style="width: 40px;"></div>' + '<div class="goods-name"><span>' + item.name + '</span></div>' + '</div>' + '<div class="goods-line"></div>' + '<div class="goods-send-url" data-url="' + url + '"> <span>发送地址</span> </div>' + '</div>';
  ChatResolver.renderHtml(html);
  $('.goods-send-url').unbind().bind('click', function () {
    var goodsUrl = $('.goods-send-url').attr("data-url");

    if (goodsUrl) {
      io.sendMsg(goodsUrl, "TEXT");
    }
  });
}

function orderData(res, count) {
  var data = "";

  for (var i = 0; i < count; i++) {
    data += "<ul>\n                    <li class=\"orderLi\">\n                        <div style=\"display: inline-block;\">\n                            <label>\u8BA2\u5355\u53F7:</label>\n                            <span>".concat(res[i].orderId, " </span>\n                            <span>").concat(res[i].orderTime, " </span>\n                        </div>\n                        <span>\n                            <b>").concat(res[i].orderStatusInfo, " </b>\n                        </span>\n                    </li>").replace(/\n/gm, "");

    for (var j = 0; j < res[i].prdInfoList.length; j++) {
      data += "<li class=\"prdLi\">\n                        <div style=\"display: flex; width: 100%;\">\n                            <div class=\"img\">\n                                <img src=\"".concat(res[i].prdInfoList[j].prdImgUrl, " \" alt=\"\"/>\n                            </div>\n                            <div class=\"myorder_content\">\n                                <a href=\"").concat(res[i].prdInfoList[j].prdDetailUrl, "\" target=\"_blank\" class=\"myorder_content_name\">\n                                    ").concat(res[i].prdInfoList[j].prdName, " \n                                </a>\n                                <p style=\"color: #999;\">\n                                    <span>\u5546\u54C1\u7F16\u53F7\uFF1A</span>\n                                    <span style=\"margin-left: -0.5rem;\">").concat(res[i].prdInfoList[j].prdCode, " </span>\n                                </p>   \n                                <p style=\"color: #999;\">\n                                    <span>\u6570\u91CF\uFF1A</span>\n                                    <span style=\"margin-left: -0.5rem;\">").concat(res[i].prdInfoList[j].prdAcount, " </span>\n                                    <b style=\"color: #000;\">\uFFE5").concat(res[i].prdInfoList[j].prdPrice, " </b>\n                                </p>\n                                <button class=\"send-order\" data-index=\"").concat(j, "\">\u53D1\u9001</button>\n                            </div>\n                        </div>\n                    </li> ").replace(/\n/gm, "");

      if (res[i].prdInfoList[j].additional) {
        for (var l = 0; l < res[i].prdInfoList[j].additional.length; l++) {
          data += "<li class=\"prdLi\">\n                                <div style=\"display: flex; width: 100%;\">\n                                    <img class=\"img\" src=\"".concat(res[i].prdInfoList[j].additional[l].prdImgUrl, "\" alt=\"\">\n                                    <div class=\"myorder_content\">\n                                        <p class=\"myorder_content_name\">\n                                            <b style=\"color:#e2231a;\">").concat(res[i].prdInfoList[j].additional[l].isGift === "1" ? "【赠品】" : res[i].prdInfoList[j].additional[l].isService === "0" ? "" : "【服务】", "</b>").concat(res[i].prdInfoList[j].additional[l].prdName, "\n                                        </p>\n                                        <p style=\"color: #959595;\">\n                                            <span>\u6570\u91CF\uFF1A</span>\n                                            <span style=\"margin-left: -8px;\">").concat(res[i].prdInfoList[j].additional[l].prdAcount, "</span>\n                                        </p>\n                                        <p style=\"display: ").concat(res[i].prdInfoList[j].additional[l].isGift === "1" ? "none" : "inline-block", ";\"><b>\uFFE5").concat(res[i].prdInfoList[j].additional[l].prdPrice, "</b></p>\n                                    </div>\n                                </div>\n                            </li> ").replace(/\n/gm, "");
        }
      }
    }

    ;
    data += "<li class=\"prdlenLi\">\n                    <div style=\"display: inline-block;\">\n                        <span>\u5171</span><span>".concat(res[i].prdInfoList.length, "</span><span style=\"padding-right: 12px;\">\u4EF6</span>\n                        <span>\u8BA2\u5355\u91D1\u989D\uFF1A</span><span style=\"color: #f4364c; margin-left: -8px\"><b>\uFFE5").concat(res[i].orderAmount, "</b></span>\n                    </div>\n                </li>\n                <li style=\"justify-content: center;\">\n                    <button class=\"view\" data-index=\"").concat(i, "\">\n                        <lable>\u67E5\u770B\u7269\u6D41</lable>\n                        <img src=\"./images/chatwap/down-arrow.png\">\n                    </button>\n                </li>\n                <li style=\"display:none\" class=\"logisticsList\"></li>\n            </ul>").replace(/\n/gm, "");
  }

  data += "<ul class=\"loading\"><p>\u75AF\u72C2\u52A0\u8F7D\u4E2D...</p></ul><ul class=\"loading_end\"><p>\u4EB2\uFF0C\u6CA1\u6709\u66F4\u591A\u4E86~</p></ul>";
  $('.myorder_main').html(data);
} // 加载订单列表


function initOrderList(num) {
  var urlParams = Common.getUrlParams(),
      ttc = urlParams.ttc ? urlParams.ttc : 1,
      params = {
    ttc: ttc,
    pageNum: pageNum,
    pageSize: 3
  };
  pageNum++, Request.jsonp(Api.orderList, params, true, function (res) {
    if (res.rc == 0) {
      if (res.data) {
        $('.myorder_nall').hide();
        arrOrder = arrOrder.concat(res.data);
        orderData(arrOrder, arrOrder.length);
        $('.myorder_main').on("scroll", scrollHandler); //绑定滚动事件

        num === 1 ? $('.loading').hide() : $('.loading').show();
      } else {
        $('.myorder_nall').show();
        $('.loading').hide();
        $('.loading_end').show();
        setTimeout(function () {
          $('.loading_end').hide();
        }, 600);
        $('.myorder_main').off("scroll", scrollHandler); //卸载滚动事件
      }
    } else {
      $('.myorder_main').off("scroll", scrollHandler); //卸载滚动事件
    }
  });
}

function logisticsData(res, data) {
  var logData = "<div class=\"package-status\"><div class=\"status-box\">\n        <ul class=\"status-list\" id=\"status-list\">\n        <div style=\"display: list-item;\"><div class=\"status-content-latest\">".concat(data.deliveryAddress.address, "</div></div>");
  logData += res.data.logistics0.map(function (item, idx, arr) {
    return "<li class=\"test ".concat(idx === 0 && data.orderStatus === "9" ? "endact" : "", " ").concat(idx === arr.length - 1 ? "firstact" : "", "\" style=\"display:block\">\n             <div class=\"status-content-before\">").concat(item.lgdesc, "</div>\n             <div class=\"status-time-before\">").concat(item.lgtime, "</div>\n        </li>");
  }).join("");
  logData += '</ul></div></div>';
  $('.logisticsList').html(logData);
}

function logisticsNull(res) {
  var logData = "<div class=\"package-status\"><div class=\"status-box\">\n        <ul class=\"status-list\" id=\"status-list\">\u6682\u65E0\u7269\u6D41\u4FE1\u606F</ul></div></div>";
  $('.logisticsList').html(logData);
} // 加载物流


function initLogistics(data) {
  var urlParams = Common.getUrlParams();
  var ttc = urlParams.ttc ? urlParams.ttc : 1;
  var params = {
    ttc: ttc,
    orderCode: data.orderId
  };
  Request.jsonp(Api.logistics, params, true, function (res) {
    if (res.rc == 0) {
      logisticsData(res, data);
    } else {
      logisticsNull(res);
    }
  });
} // 上滑事件


function scrollHandler() {
  $('.loading').show();

  if ($(this)[0].scrollTop + $(this).height() >= $(this)[0].scrollHeight) {
    clearTimeout(timers);
    timers = setTimeout(function () {
      initOrderList(1);
      off_on = true;
    }, 800);
  }
}

var customer;
var io;
var isMobile = 2;
var ioType = window.WebSocket ? true : false;
$(function () {
  document.domain = Config.domain;
  auth();
});

function auth() {
  var urlParams = Common.getUrlParams();
  var ttc = urlParams.ttc ? urlParams.ttc : 1;
  var skc = urlParams.skc;
  var buc = urlParams.buc;
  var gc = urlParams.gc;
  var params = {
    ttc: ttc,
    skc: skc,
    buc: buc,
    gc: gc,
    device: isMobile,
    origin: 1,
    io: ioType ? "ws" : "poll"
  };
  Request.jsonp(Api.auth, params, false, function (res) {
    switch (res.rc) {
      // 正常
      case 0:
        customer = res.data;
        buildChat(customer);
        initChat();
        ChatWap.startChat();
        customer.real === "1" ? $('.mark_order').css("display", "inline-block") : $('.mark_order').css("display", "none");

        if (res.skn && res.skn.indexOf('EFF') !== -1) {
          params.ttc = 3;
        }

        break;
      // 需要登录

      case 1:
        var encodeURISearch = encodeURIComponent(window.location.href); //编码地址

        res.data && res.data.loginUrl ? window.open(res.data.loginUrl + '&ru=' + encodeURISearch, "_self") : Pdialog.showDialog({
          content: res.rm
        });
        break;
      // 非工作时间

      case 2:
        var tipMessage = '';
        res.data && res.data.offlineMsg ? tipMessage = res.data.offlineMsg : tipMessage = res.rm;
        Pdialog.showDialog({
          content: tipMessage
        });
        $("#chat_service_status_msg").text('客服非工作时间！');
        initChat();
        break;
      // 无法正确路由到skc

      case 3:
        var host = window.location.host;
        window.location.href = '//' + host;
        break;

      case 5:
        $('.app-main').hide();
        Pdialog.showDialog({
          content: "由于在咨询过程中，咨询内容包含不合法信息，已暂时限制咨询请求！"
        });
        break;
      // 地址问题

      default:
        Pdialog.showDialog({
          content: "初始化信息失败，请重试！"
        });
        break;
    }
  }, function (res) {
    Pdialog.showDialog({
      content: "初始化信息失败，请重试！"
    });
  });
}

function buildChat(customer) {
  if (ioType) {
    io = WsClient({
      url: Config.ws + '/ws.io',
      customer: customer
    });
  } else {
    io = HpClient({
      url: Config.poll + '/poll.io',
      customer: customer
    });
  }
}