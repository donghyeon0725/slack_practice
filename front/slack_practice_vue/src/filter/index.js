import Vue from 'vue';
import { formatDate, dayAgo } from '@/util/date';
import { cutTextAtStart } from '@/util/text';

Vue.filter('formatDate', formatDate);
Vue.filter('dayAgo', dayAgo);
Vue.filter('cutTextAtStart', cutTextAtStart);
