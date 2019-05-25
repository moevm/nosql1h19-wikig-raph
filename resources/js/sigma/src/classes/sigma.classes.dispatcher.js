(function(){"use strict";var n=function(){Object.defineProperty(this,"_handlers",{value:{}})};n.prototype.bind=function(t,e){var s,n,h,i;if(1===arguments.length&&"object"==typeof t)for(t in t)this.bind(t,t[t]);else{if(2!==arguments.length||"function"!=typeof e)throw"bind: Wrong arguments.";for(s=0,n=(i="string"==typeof t?t.split(" "):t).length;s!==n;s+=1)(h=i[s])&&(this._handlers[h]||(this._handlers[h]=[]),this._handlers[h].push({handler:e}))}return this},n.prototype.unbind=function(t,e){var s,n,h,i,r,o,l,a="string"==typeof t?t.split(" "):t;if(!arguments.length){for(r in this._handlers)delete this._handlers[r];return this}if(e)for(s=0,n=a.length;s!==n;s+=1){if(l=a[s],this._handlers[l]){for(o=[],h=0,i=this._handlers[l].length;h!==i;h+=1)this._handlers[l][h].handler!==e&&o.push(this._handlers[l][h]);this._handlers[l]=o}this._handlers[l]&&0===this._handlers[l].length&&delete this._handlers[l]}else for(s=0,n=a.length;s!==n;s+=1)delete this._handlers[a[s]];return this},n.prototype.dispatchEvent=function(t,e){var s,n,h,i,r,o,l,a="string"==typeof t?t.split(" "):t;for(e=void 0===e?{}:e,s=0,n=a.length;s!==n;s+=1)if(l=a[s],this._handlers[l]){for(o=this.getEvent(l,e),r=[],h=0,i=this._handlers[l].length;h!==i;h+=1)this._handlers[l][h].handler(o),this._handlers[l][h].one||r.push(this._handlers[l][h]);this._handlers[l]=r}return this},n.prototype.getEvent=function(t,e){return{type:t,data:e||{},target:this}},n.extend=function(t,e){var s;for(s in n.prototype)n.prototype.hasOwnProperty(s)&&(t[s]=n.prototype[s]);n.apply(t,e)},void 0!==this.sigma?(this.sigma.classes=this.sigma.classes||{},this.sigma.classes.dispatcher=n):"undefined"!=typeof exports?("undefined"!=typeof module&&module.exports&&(exports=module.exports=n),exports.dispatcher=n):this.dispatcher=n}).call(this);