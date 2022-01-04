package de.iani.cubesideutils;

import java.util.Objects;

public class Triple<T, S, U> {

    public final T first;
    public final S second;
    public final U third;

    public Triple(T first, S second, U third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T first() {
        return this.first;
    }

    public <X> Triple<X, S, U> setFirst(X first) {
        return new Triple<>(first, this.second, this.third);
    }

    public S second() {
        return this.second;
    }

    public <X> Triple<T, X, U> setSecond(X second) {
        return new Triple<>(first, second, this.third);
    }

    public U third() {
        return this.third;
    }

    public <X> Triple<T, S, X> setThird(X third) {
        return new Triple<>(first, this.second, third);
    }

    @Override
    public String toString() {
        return "(" + this.first + ";" + this.second + ";" + this.third + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.first, this.second, this.third);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Triple<?, ?, ?>)) {
            return false;
        }
        Triple<?, ?, ?> op = (Triple<?, ?, ?>) other;
        return Objects.equals(this.first, op.first) && Objects.equals(this.second, op.second) && Objects.equals(this.third, op.third);
    }

}
