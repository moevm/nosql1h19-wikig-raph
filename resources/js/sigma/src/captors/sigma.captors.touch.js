(function(t){"use strict";if("undefined"==typeof sigma)throw"sigma is not declared";sigma.utils.pkg("sigma.captors"),sigma.captors.touch=function(t,e,a){var f,p,E,b,C,k,L,D,M,T,P,w,q,I,O,R,Z=this,i=t,z=e,V=a,X=[];function Y(t){var e=sigma.utils.getOffset(i);return{x:t.pageX-e.left,y:t.pageY-e.top}}function n(t){var e,a,i,n,o,s;if(V("touchEnabled"))switch((X=t.touches).length){case 1:z.isMoving=!0,q=1,f=z.x,p=z.y,C=z.x,k=z.y,o=Y(X[0]),L=o.x,D=o.y;break;case 2:return z.isMoving=!0,q=2,o=Y(X[0]),s=Y(X[1]),e=o.x,i=o.y,a=s.x,n=s.y,C=z.x,k=z.y,E=z.angle,b=z.ratio,f=z.x,p=z.y,L=e,D=i,M=a,T=n,P=Math.atan2(T-D,M-L),w=Math.sqrt((T-D)*(T-D)+(M-L)*(M-L)),t.preventDefault(),!1}}function o(t){if(V("touchEnabled")){X=t.touches;var e=V("touchInertiaRatio");switch(R&&(I=!1,clearTimeout(R)),q){case 2:if(1===t.touches.length){n(t),t.preventDefault();break}case 1:z.isMoving=!1,Z.dispatchEvent("stopDrag"),I&&(O=!1,sigma.misc.animation.camera(z,{x:z.x+e*(z.x-C),y:z.y+e*(z.y-k)},{easing:"quadraticOut",duration:V("touchInertiaDuration")})),I=!1,q=0}}}function s(t){if(!O&&V("touchEnabled")){var e,a,i,n,o,s,u,c,r,l,d,h,g,m,v,x,y;switch(X=t.touches,I=!0,R&&clearTimeout(R),R=setTimeout(function(){I=!1},V("dragTimeout")),q){case 1:e=(c=Y(X[0])).x,i=c.y,l=z.cameraPosition(e-L,i-D,!0),m=f-l.x,v=p-l.y,m===z.x&&v===z.y||(C=z.x,k=z.y,z.goTo({x:m,y:v}),Z.dispatchEvent("mousemove",sigma.utils.mouseCoords(t,c.x,c.y)),Z.dispatchEvent("drag"));break;case 2:c=Y(X[0]),r=Y(X[1]),e=c.x,i=c.y,a=r.x,n=r.y,d=z.cameraPosition((L+M)/2-sigma.utils.getCenter(t).x,(D+T)/2-sigma.utils.getCenter(t).y,!0),u=z.cameraPosition((e+a)/2-sigma.utils.getCenter(t).x,(i+n)/2-sigma.utils.getCenter(t).y,!0),h=Math.atan2(n-i,a-e)-P,g=Math.sqrt((n-i)*(n-i)+(a-e)*(a-e))/w,e=d.x,i=d.y,x=b/g,i*=g,y=E-h,a=(e*=g)*(o=Math.cos(-h))+i*(s=Math.sin(-h)),i=n=i*o-e*s,m=(e=a)-u.x+f,v=i-u.y+p,x===z.ratio&&y===z.angle&&m===z.x&&v===z.y||(C=z.x,k=z.y,z.angle,z.ratio,z.goTo({x:m,y:v,angle:y,ratio:x}),Z.dispatchEvent("drag"))}return t.preventDefault(),!1}}sigma.classes.dispatcher.extend(this),sigma.utils.doubleClick(i,"touchstart",function(t){var e,a,i;if(t.touches&&1===t.touches.length&&V("touchEnabled"))return O=!0,a=1/V("doubleClickZoomingRatio"),e=Y(t.touches[0]),Z.dispatchEvent("doubleclick",sigma.utils.mouseCoords(t,e.x,e.y)),V("doubleClickEnabled")&&(e=z.cameraPosition(e.x-sigma.utils.getCenter(t).x,e.y-sigma.utils.getCenter(t).y,!0),i={duration:V("doubleClickZoomDuration"),onComplete:function(){O=!1}},sigma.utils.zoomTo(z,e.x,e.y,a,i)),t.preventDefault?t.preventDefault():t.returnValue=!1,t.stopPropagation(),!1}),i.addEventListener("touchstart",n,!1),i.addEventListener("touchend",o,!1),i.addEventListener("touchcancel",o,!1),i.addEventListener("touchleave",o,!1),i.addEventListener("touchmove",s,!1),this.kill=function(){sigma.utils.unbindDoubleClick(i,"touchstart"),i.addEventListener("touchstart",n),i.addEventListener("touchend",o),i.addEventListener("touchcancel",o),i.addEventListener("touchleave",o),i.addEventListener("touchmove",s)}}}).call(this);