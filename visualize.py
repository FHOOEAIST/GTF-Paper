import plotly.express as px
import pandas as pd

if __name__ == '__main__':
    cnt = 10
    df = pd.read_csv(r"C:\Public\git\_github\GTF-Paper\versionsWithVersion.txt", sep=";", nrows=cnt).sort_values('Count', ascending=True)
    fig = px.bar(df, x="Count", y="Dependency", orientation='h', title=f"Top {cnt} used dependencies", text="Count",
                 width=1000, height=500, labels={"Count": "Number of times dependency is (transitive) referenced", "Dependency": "Dependency <groupId>:<artifactId>"}, template="simple_white")
    fig.update_traces(marker_color='black')
    fig.show()
    fig.write_image("top_dependencies.pdf")
