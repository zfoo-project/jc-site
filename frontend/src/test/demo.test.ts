import { add } from "./demo";
import Demo from "./Demo.vue";
import { mount } from "@vue/test-utils";
import _ from "lodash";

function seed(): string{
  const a = _.random(10_0000_0000, 19_0000_0000);
  const b = _.random(1_0000_0000, 1_9000_0000);
  const seed = _.toString(a) + _.toString(b);
  return seed;
}

// demo1
test("first", () => {
  console.log(seed());
});

// function
test("second", () => {
  expect(add(1, 2)).toBe(3);
});

// component1
test("third", () => {
  expect(Demo).toBeTruthy();
});

// component2
test("fourth", () => {
  console.log(Demo);
});

// component3
test("fifth", () => {
  const wrapper = mount(Demo, {
    props: {
      name: "YJK",
    },
  });

  expect(wrapper.text()).toContain("YJK");
});
