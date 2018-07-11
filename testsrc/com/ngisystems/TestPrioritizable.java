package com.ngisystems;

import java.util.Objects;

class TestPrioritizable implements Prioritizable, Comparable<TestPrioritizable>{
    private String value;
    private Integer priority;

    public TestPrioritizable(String value, Integer priority) {
        this.value = value;
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(TestPrioritizable o) {
        return value.compareTo(o.value);
    }

    @Override
    public String toString() {
        return value + "-" + priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestPrioritizable that = (TestPrioritizable) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(value);
    }
}
