(function(a){"use strict";var s={pointToSquare:function(e){return{x1:e.x-e.size,y1:e.y-e.size,x2:e.x+e.size,y2:e.y-e.size,height:2*e.size}},isAxisAligned:function(e){return e.x1===e.x2||e.y1===e.y2},axisAlignedTopPoints:function(e){return e.y1===e.y2&&e.x1<e.x2?e:e.x1===e.x2&&e.y2>e.y1?{x1:e.x1-e.height,y1:e.y1,x2:e.x1,y2:e.y1,height:e.height}:e.x1===e.x2&&e.y2<e.y1?{x1:e.x1,y1:e.y2,x2:e.x2+e.height,y2:e.y2,height:e.height}:{x1:e.x2,y1:e.y1-e.height,x2:e.x1,y2:e.y1-e.height,height:e.height}},lowerLeftCoor:function(e){var t=Math.sqrt(Math.pow(e.x2-e.x1,2)+Math.pow(e.y2-e.y1,2));return{x:e.x1-(e.y2-e.y1)*e.height/t,y:e.y1+(e.x2-e.x1)*e.height/t}},lowerRightCoor:function(e,t){return{x:t.x-e.x1+e.x2,y:t.y-e.y1+e.y2}},rectangleCorners:function(e){var t=this.lowerLeftCoor(e),i=this.lowerRightCoor(e,t);return[{x:e.x1,y:e.y1},{x:e.x2,y:e.y2},{x:t.x,y:t.y},{x:i.x,y:i.y}]},splitSquare:function(e){return[[{x:e.x,y:e.y},{x:e.x+e.width/2,y:e.y},{x:e.x,y:e.y+e.height/2},{x:e.x+e.width/2,y:e.y+e.height/2}],[{x:e.x+e.width/2,y:e.y},{x:e.x+e.width,y:e.y},{x:e.x+e.width/2,y:e.y+e.height/2},{x:e.x+e.width,y:e.y+e.height/2}],[{x:e.x,y:e.y+e.height/2},{x:e.x+e.width/2,y:e.y+e.height/2},{x:e.x,y:e.y+e.height},{x:e.x+e.width/2,y:e.y+e.height}],[{x:e.x+e.width/2,y:e.y+e.height/2},{x:e.x+e.width,y:e.y+e.height/2},{x:e.x+e.width/2,y:e.y+e.height},{x:e.x+e.width,y:e.y+e.height}]]},axis:function(e,t){return[{x:e[1].x-e[0].x,y:e[1].y-e[0].y},{x:e[1].x-e[3].x,y:e[1].y-e[3].y},{x:t[0].x-t[2].x,y:t[0].y-t[2].y},{x:t[0].x-t[1].x,y:t[0].y-t[1].y}]},projection:function(e,t){var i=(e.x*t.x+e.y*t.y)/(Math.pow(t.x,2)+Math.pow(t.y,2));return{x:i*t.x,y:i*t.y}},axisCollision:function(e,t,i){for(var n=[],r=[],h=0;h<4;h++){var x=this.projection(t[h],e),s=this.projection(i[h],e);n.push(x.x*e.x+x.y*e.y),r.push(s.x*e.x+s.y*e.y)}var y=Math.max.apply(Math,n),o=Math.max.apply(Math,r),a=Math.min.apply(Math,n);return Math.min.apply(Math,r)<=y&&a<=o},collision:function(e,t){for(var i=this.axis(e,t),n=!0,r=0;r<4;r++)n=n&&this.axisCollision(i[r],e,t);return n}};function y(e,t){for(var i=[],n=0;n<4;n++)e.x2>=t[n][0].x&&e.x1<=t[n][1].x&&e.y1+e.height>=t[n][0].y&&e.y1<=t[n][2].y&&i.push(n);return i}function o(e,t){for(var i=[],n=0;n<4;n++)s.collision(e,t[n])&&i.push(n);return i}function x(e,t){var i,n,r=t.level+1,h=Math.round(t.bounds.width/2),x=Math.round(t.bounds.height/2),s=Math.round(t.bounds.x),y=Math.round(t.bounds.y);switch(e){case 0:i=s,n=y;break;case 1:i=s+h,n=y;break;case 2:i=s,n=y+x;break;case 3:i=s+h,n=y+x}return l({x:i,y:n,width:h,height:x},r,t.maxElements,t.maxLevel)}function u(e,t,i){if(i.level<i.maxLevel)for(var n=y(t,i.corners),r=0,h=n.length;r<h;r++)i.nodes[n[r]]===a&&(i.nodes[n[r]]=x(n[r],i)),u(e,t,i.nodes[n[r]]);else i.elements.push(e)}function l(e,t,i,n){return{level:t||0,bounds:e,corners:s.splitSquare(e),maxElements:i||20,maxLevel:n||4,elements:[],nodes:[]}}var e=function(){this._geom=s,this._tree=null,this._cache={query:!1,result:!1}};e.prototype.index=function(e,t){if(!t.bounds)throw"sigma.classes.quad.index: bounds information not given.";var i=t.prefix||"";this._tree=l(t.bounds,0,t.maxElements,t.maxLevel);for(var n=0,r=e.length;n<r;n++)u(e[n],s.pointToSquare({x:e[n][i+"x"],y:e[n][i+"y"],size:e[n][i+"size"]}),this._tree);return this._cache={query:!1,result:!1},this._tree},e.prototype.point=function(e,t){return this._tree&&function e(t,i){if(i.level<i.maxLevel){var n=(r=t,h=i.bounds,x=h.x+h.width/2,s=h.y+h.height/2,y=r.y<s,o=r.x<x,y?o?0:1:o?2:3);return i.nodes[n]!==a?e(t,i.nodes[n]):[]}return i.elements;var r,h,x,s,y,o}({x:e,y:t},this._tree)||[]},e.prototype.area=function(e){var t,i,n=JSON.stringify(e);if(this._cache.query===n)return this._cache.result;i=s.isAxisAligned(e)?(t=y,s.axisAlignedTopPoints(e)):(t=o,s.rectangleCorners(e));var r=this._tree?function e(t,i,n,r){if(r=r||{},i.level<i.maxLevel)for(var h=n(t,i.corners),x=0,s=h.length;x<s;x++)i.nodes[h[x]]!==a&&e(t,i.nodes[h[x]],n,r);else for(var y=0,o=i.elements.length;y<o;y++)r[i.elements[y].id]===a&&(r[i.elements[y].id]=i.elements[y]);return r}(i,this._tree,t):[],h=[];for(var x in r)h.push(r[x]);return this._cache.query=n,this._cache.result=h},void 0!==this.sigma?(this.sigma.classes=this.sigma.classes||{},this.sigma.classes.quad=e):"undefined"!=typeof exports?("undefined"!=typeof module&&module.exports&&(exports=module.exports=e),exports.quad=e):this.quad=e}).call(this);