(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-9b1d3ec2"],{"560f":function(t,e,n){"use strict";n.r(e);var r=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{staticStyle:{position:"absolute",top:"50%",left:"50%",transform:"translate(-50%, -50%)"}},[n("SignupForm")],1)},a=[],i=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{staticStyle:{width:"300px"}},[t.show?n("b-form",{on:{submit:t.onSubmit,reset:t.onReset}},[n("b-form-group",{staticClass:"mb-2",attrs:{id:"input-group-1",label:"Email address:","label-for":"input-1",description:t.emailValidation}},[n("b-form-input",{attrs:{id:"input-1",type:"email",placeholder:"Enter email",required:"",autocomplete:"off"},model:{value:t.form.email,callback:function(e){t.$set(t.form,"email",e)},expression:"form.email"}})],1),n("b-button",{attrs:{type:"submit",variant:"primary"}},[t._v("Submit")])],1):t._e()],1)},o=[],s=n("1da1"),u=(n("96cf"),n("3786")),l=n("9941"),c={data:function(){return{form:{email:""},description:"",show:!0}},methods:{onSubmit:function(t){var e=this;return Object(s["a"])(regeneratorRuntime.mark((function n(){var r,a;return regeneratorRuntime.wrap((function(n){while(1)switch(n.prev=n.next){case 0:return t.preventDefault(),n.prev=1,n.next=4,Object(u["d"])(e.form.email);case 4:return r=n.sent,a=r.status,200==a&&alert("".concat(e.form.email,"으로 메일을 전송 했습니다. 확인 부탁드립니다.")),n.next=9,e.$router.push("/");case 9:return n.abrupt("return");case 12:n.prev=12,n.t0=n["catch"](1),alert(n.t0.response.data.message);case 15:case"end":return n.stop()}}),n,null,[[1,12]])})))()},onReset:function(t){var e=this;t.preventDefault(),this.form.email="",this.form.checked=[],this.show=!1,this.$nextTick((function(){e.show=!0}))}},computed:{emailValidation:function(){return!Object(l["d"])(this.form.email)&&this.form.email?"this email does not validate":this.form.email?"email validate":"input email that you want to join"}}},m=c,p=n("2877"),f=Object(p["a"])(m,i,o,!1,null,null,null),d=f.exports,h={name:"app",components:{SignupForm:d}},b=h,v=(n("61d9"),Object(p["a"])(b,r,a,!1,null,null,null));e["default"]=v.exports},"61d9":function(t,e,n){"use strict";n("e68d")},e68d:function(t,e,n){}}]);
//# sourceMappingURL=chunk-9b1d3ec2.d80684d4.js.map